package com.i0dev.utility;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class PluginMessageUtil {

    @SneakyThrows
    public static void sendMessage(String specialChannel, String data) {
        com.google.common.io.ByteArrayDataOutput out = com.google.common.io.ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(specialChannel);
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        msgout.writeUTF(data);
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        LogUtil.log("Sent Plugin message: [" + data + "]");
        com.i0dev.BotPlugin.get().getProxy().getServersCopy().forEach((s, serverInfo) -> serverInfo.sendData("BungeeCord", out.toByteArray()));
    }

}
