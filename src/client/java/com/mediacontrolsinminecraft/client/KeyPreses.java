package com.mediacontrolsinminecraft.client;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public abstract class KeyPreses {

    // Native Windows Virtual-Key Constants
    public static final byte VK_MEDIA_PLAY_PAUSE = (byte) 0xB3;
    public static final byte VK_MEDIA_NEXT_TRACK = (byte) 0xB0;
    public static final byte VK_MEDIA_PREV_TRACK = (byte) 0xB1;

    public static void sendKeyPress(byte vKey) {
        // 1. Create an array of 2 inputs: one for Key Down, one for Key Up
        WinUser.INPUT[] inputs = (WinUser.INPUT[]) new WinUser.INPUT().toArray(2);

        // --- STEP 2: Configure KEY DOWN Event ---
        inputs[0].type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
        inputs[0].input.setType(WinUser.KEYBDINPUT.class);
        inputs[0].input.ki.wVk = new WinDef.WORD(vKey);
        inputs[0].input.ki.wScan = new WinDef.WORD(0);
        inputs[0].input.ki.dwFlags = new WinDef.DWORD(WinUser.KEYBDINPUT.KEYEVENTF_EXTENDEDKEY);
        inputs[0].input.ki.time = new WinDef.DWORD(0);
        inputs[0].input.ki.dwExtraInfo = null;

        // --- STEP 3: Configure KEY UP Event ---
        inputs[1].type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
        inputs[1].input.setType(WinUser.KEYBDINPUT.class);
        inputs[1].input.ki.wVk = new WinDef.WORD(vKey);
        inputs[1].input.ki.wScan = new WinDef.WORD(0);
        // Combine Extended Key and Key Up flags
        inputs[1].input.ki.dwFlags = new WinDef.DWORD(WinUser.KEYBDINPUT.KEYEVENTF_EXTENDEDKEY | WinUser.KEYBDINPUT.KEYEVENTF_KEYUP);
        inputs[1].input.ki.time = new WinDef.DWORD(0);
        inputs[1].input.ki.dwExtraInfo = null;

        // --- STEP 4: Send array to Windows ---
        // SendInput parameters: (Number of inputs, Array of inputs, Size of a single INPUT structure)
        WinDef.DWORD result = User32.INSTANCE.SendInput(new WinDef.DWORD(2), inputs, inputs[0].size());

        if (result.intValue() == 0) {
            System.err.println("Failed to send key input. Blocked by UIPI or permissions.");
        } else {
            System.out.println("Media event injected successfully!");
        }

    }

}
