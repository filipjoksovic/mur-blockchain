package blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private final int index;
    private final Long timestamp;
    private String data;
    private final String previousHash;
    private final String hash;

    public Block(int index, String previousHash, String data) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();

    }

    public int getIndex() {
        return index;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public String calculateHash() {

        String text = index + previousHash + timestamp + data;


        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        final byte[] bytes = digest.digest(text.getBytes());

        final StringBuilder hexBuilder = new StringBuilder();
        for (final byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexBuilder.append('0');
            }
            hexBuilder.append(hex);
        }


        return hexBuilder.toString();
    }
}
