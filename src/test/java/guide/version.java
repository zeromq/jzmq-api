package guide;

import org.zeromq.jzmq.ManagedContext;

public class version {

    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        System.out.println("context.getVersionString() = " + context.getVersionString());
        System.out.println("context.getFullVersion() = " + context.getFullVersion());
    }
}
