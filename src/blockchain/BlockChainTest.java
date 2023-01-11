package blockchain;

import java.util.Random;

public class BlockChainTest {
    public static void main(String[] args) throws InterruptedException {

        BlockUtils blockUtils = new BlockUtils();
        Random random = new Random();
        while (true) {
            blockUtils.addBlock(String.valueOf(random.nextInt(5000)));
            if (blockUtils.validateChain()) {
                System.out.println("[INFO]: Blockchain still valid.");
            } else {
                System.out.println("[ERROR]: Blockchain not valid.");
                return;
            }
            System.out.println("[INFO]: Number of blocks: " + blockUtils.getBlockChain().size());

        }


    }
}