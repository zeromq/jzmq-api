package org.zeromq.client;

import org.zeromq.Patterns;
import org.zeromq.api.BinaryStarReactor;

public class CloneServerMain {
    /**
     * @param args
     */
    public static void main(String[] args) {
        assert (args.length == 1);

        BinaryStarReactor.Mode mode = (args[0].equals("--primary"))
            ? BinaryStarReactor.Mode.PRIMARY
            : BinaryStarReactor.Mode.BACKUP;
        Patterns.newCloneServer(mode, "localhost", "localhost");
    }
}
