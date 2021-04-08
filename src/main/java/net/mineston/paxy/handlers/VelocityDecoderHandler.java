package net.mineston.paxy.handlers;

import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.mineston.paxy.utils.BufUtils;

import java.io.IOException;
import java.util.List;

public class VelocityDecoderHandler extends MessageToMessageDecoder<ByteBuf> {

    private final PaxyProtocol protocol;

    protected VelocityDecoderHandler(PaxyProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        msg.retain();
        {
            ByteBuf packetBuf = msg.retainedSlice();
            final int packetId = BufUtils.readVarInt(packetBuf);
            Packet packet = protocol.createIncomingPacket(packetId);

            try {
                packet.read(new ByteBufNetInput(packetBuf));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("server packet: " + packet.getClass().getSimpleName());

            if (packet instanceof LoginSuccessPacket) {
                protocol.setSubProtocol(SubProtocol.GAME);
            }
        }

        out.add(msg);
    }
}
