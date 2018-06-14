package ch7;

import org.apache.jute.*;
import org.apache.zookeeper.server.ByteBufferInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author pfjia
 * @since 2018/6/15 0:10
 */
public class MockReqHeader implements Record {
    private long sessionId;
    private String type;

    public MockReqHeader() {
    }

    public MockReqHeader(long sessionId, String type) {
        this.sessionId = sessionId;
        this.type = type;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {
        archive.startRecord(this, tag);
        archive.writeLong(sessionId, "sessionId");
        archive.writeString(type, "type");
        archive.endRecord(this, tag);
    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {
        archive.startRecord(tag);
        sessionId = archive.readLong("sessionId");
        type = archive.readString("type");
        archive.endRecord(tag);
    }


    @Override
    public String toString() {
        return "MockReqHeader{" +
                "sessionId=" + sessionId +
                ", type='" + type + '\'' +
                '}';
    }

    public static void main(String[] args) throws IOException {
        //开始序列化
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive binaryOutputArchive = BinaryOutputArchive.getArchive(baos);
        new MockReqHeader(123456L, "ping").serialize(binaryOutputArchive, "header");
        //模拟网络传输
        ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
        //反序列化
        ByteBufferInputStream bbis = new ByteBufferInputStream(bb);
        BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
        MockReqHeader h = new MockReqHeader();
        h.deserialize(bbia, "header");
        System.out.println(h);
        bbis.close();
        baos.close();

    }
}
