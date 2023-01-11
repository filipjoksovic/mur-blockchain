package blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Block {
    private final int index;
    private final Long timestamp;
    private String data;
    private final String previousHash;
    private String hash;

    private Integer nonce;

    private Integer difficulty = 3;

    public Block(int index, String previousHash, String data) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public Block(String[] data) {
        this.index = Integer.parseInt(data[0]);
        this.timestamp = Long.parseLong(data[1]);
        this.data = data[2];
        this.previousHash = data[3];
        this.hash = data[4];
        this.nonce = Integer.parseInt(data[5]);
        this.difficulty = Integer.parseInt(data[6]);
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

        String text = index + previousHash + timestamp + data + nonce;


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

    public String getLeadingZeros(int diff) {
        String s = "";
        for (int i = 0; i < diff; i++) {
            s += "0";
        }
        return s;
    }

    public String getData() {
        return data;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public synchronized void mineBlock(int difficulty) {
        String leadingZeros = getLeadingZeros(difficulty);
        while (!this.hash.substring(0, difficulty).equals(leadingZeros)) {
            this.nonce++;

            this.hash = calculateHash();
        }
//        System.out.println(this.hash);
//        System.out.println("Block mined");

//        return new Block()
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index && timestamp.equals(block.timestamp) && data.equals(block.data) && previousHash.equals(block.previousHash) && hash.equals(block.hash) && nonce.equals(block.nonce) && difficulty.equals(block.difficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, timestamp, data, previousHash, hash, nonce, difficulty);
    }
}
