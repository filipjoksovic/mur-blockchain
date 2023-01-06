package blockchain;

public class BlockChainTest {
    public static void main(String[] args) {
        BlockUtils blockUtils = new BlockUtils();
        blockUtils.addBlock("100");
        blockUtils.addBlock("200");
        blockUtils.addBlock("300");

        System.out.println(blockUtils.validateChain());
        blockUtils.getBlockChain().get(2).setData("5000");
        System.out.println(blockUtils.validateChain());
    }
}