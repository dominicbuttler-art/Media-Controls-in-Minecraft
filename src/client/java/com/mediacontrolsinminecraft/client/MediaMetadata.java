package com.mediacontrolsinminecraft.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MediaMetadata {

    public static boolean mediaBeingPlayed = false;
    public static String mediaMetaData = "STATUS: No media being played";

    public static boolean powershellRunning = false;

    public static final String noMediaBeingPlayedMessageWindows = "STATUS: No media being played";
    private static final String windowsCommand =
                    "$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime];" +
                    "$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties, Windows.Media.Control, ContentType=WindowsRuntime];" +
                    "Add-Type -AssemblyName System.Runtime.WindowsRuntime;" +

                    // Explicitly resolve types safely after assemblies are loaded
                    "$extType     = [type]'System.WindowsRuntimeSystemExtensions';" +
                    "$managerType = [type]'Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager';" +
                    "$propsType   = [type]'Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties';" +

                    // Reflectively find the AsTask method
                    "$m = $extType.GetMethods().Where({" +
                    "    $_.Name -eq 'AsTask' -and $_.IsGenericMethod -and $_.GetParameters()[0].ParameterType.Name -like '*IAsyncOperation*'" +
                    "})[0];" +

                    "$lastMedia = '';" +
                    "$loopCount = 0;" +
                    "while ($true) {" +
                    "    $loopCount++;" +
                    "    try {" +
                    "        $sOp = $managerType::RequestAsync();" +
                    "        $sTask = $m.MakeGenericMethod($managerType).Invoke($null, @($sOp));" +
                    "        $sTask.Wait();" +
                    "        $session = $sTask.Result.GetCurrentSession();" +
                    "        if ($session) {" +
                    "            $pOp = $session.TryGetMediaPropertiesAsync();" +
                    "            $pTask = $m.MakeGenericMethod($propsType).Invoke($null, @($pOp));" +
                    "            $pTask.Wait();" +
                    "            $res = $pTask.Result;" +
                    "            if ($res.Title) {" +
                    "                $current = $res.Title + ' - ' + $res.Artist;" +
                    "                if ($current -ne $lastMedia) {" +
                    "                    Write-Output $current;" +
                    "                    $lastMedia = $current;" +
                    "                    [System.Console]::Out.Flush();" +
                    "                }" +
                    "            }" +
                    "        } else {" +
                    "            if ($lastMedia -ne 'No active media') {" +
                    "                Write-Output '" + noMediaBeingPlayedMessageWindows + "';" +
                    "                $lastMedia = 'No active media';" +
                    "                [System.Console]::Out.Flush();" +
                    "            }" +
                    "        }" +
                    "    } catch {" +
                    "        Write-Output ('ERROR: ' + $_.Exception.Message);" +
                    "        [System.Console]::Out.Flush();" +
                    "    }" +
                    "    if ($loopCount -ge 10) {" +
                    "        [System.GC]::Collect();" +
                    "        [System.GC]::WaitForPendingFinalizers();" +
                    "        $loopCount = 0;" +
                    "     }" +
                    "    Start-Sleep -Milliseconds 400;" +
                    "    Clear-Host" +
                    "}";

    private static ProcessBuilder powershellProcessBuilder;
    private static Process powershellProcess;

    public static void startMediaMonitoringForWindows() {

        if (!powershellRunning) {

            powershellRunning = true;

            //To shut down powershell successfully and safely without the game crashing
            ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {

                if (powershellRunning){

                    powershellRunning = false;
                    powershellProcess.destroyForcibly();

                    try {

                        if (powershellProcess.waitFor(2, TimeUnit.SECONDS)) {

                            System.out.println("PowerShell process safely terminated.");

                        }
                        else {

                            System.err.println("PowerShell failed to terminate in time.");

                        }
                    }
                    catch (InterruptedException e) {

                        Thread.currentThread().interrupt();

                    }

                }

            });

            System.out.println("Starting Java wrapper... Attempting to launch PowerShell.");

            new Thread(() -> {

                try {

                    powershellProcessBuilder = new ProcessBuilder("powershell.exe", "-NoProfile", "-Command", windowsCommand);
                    powershellProcessBuilder.redirectErrorStream(true); // Directs stderr into stdout
                    powershellProcess = powershellProcessBuilder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(powershellProcess.getInputStream()));
                    String line;

                    try {
                        while ((line = reader.readLine()) != null && powershellRunning) {

                            //Checks if media is being played
                            mediaBeingPlayed = !line.equals(noMediaBeingPlayedMessageWindows);

                            // For debugging only
//                            System.out.println("[OS Stream] " + line);

                            mediaMetaData = line;

                        }
                    } catch (Exception e) {
                        powershellProcess.destroyForcibly();
                        e.printStackTrace();
                    }

                    System.out.println("System Alert: The PowerShell background process terminated.");
                    powershellRunning = false;

                } catch (Exception e) {
                    System.out.println("Java Exception occurred:");
                    e.printStackTrace();
                }

            }).start();
        }
        else {

            System.err.println("startMediaMonitoring was called but a PowerShell process is already running");

        }

    }

    public static final String noMediaBeingPlayedMessageLinux = "No player could handle this command";

    public static void startMediaMentoringForLinux(){

        //To stop the loop when the game stops
        AtomicBoolean clientRunning = new AtomicBoolean(true);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> clientRunning.set(false));

        new Thread(() -> {

            while (clientRunning.get()){

                try {

                    String line = getNowPlayingLinux();

                    //Checks if media is being played
                    if (!line.equals(noMediaBeingPlayedMessageLinux)){

                        mediaBeingPlayed = true;

                    }
                    else {

                        mediaBeingPlayed = false;
                        line = noMediaBeingPlayedMessageWindows;

                    }

                    // For debugging only
//                    System.out.println("[OS Stream] " + line);
                    mediaMetaData = line;

                } catch (IOException e) {

                    e.printStackTrace();

                }

                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    private static String getNowPlayingLinux() throws IOException {

        ProcessBuilder playerctlProcessBuilder = new ProcessBuilder("/usr/bin/playerctl",
                "metadata",
                "--format",
                "{{ title }} - {{ artist }}");
        playerctlProcessBuilder.redirectErrorStream(true);

        Process playerctlProcess = playerctlProcessBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(playerctlProcess.getInputStream()));

        return reader.readLine();

    }

}
