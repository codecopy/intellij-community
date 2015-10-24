package com.siyeh.igtest.errorhandling.throw_from_finally_block;

import java.io.FileInputStream;
import java.io.IOException;

public class ThrowFromFinallyBlock
{
    public void foo() throws Exception
    {
        try
        {
            return;
        }
        finally
        {
            <warning descr="'throw' inside 'finally' block">throw</warning> new Exception();
        }
    }

    public void bar() throws Exception
    {
        try
        {
            return;
        }
        finally
        {
            try
            {
                <warning descr="'throw' inside 'finally' block">throw</warning> new Exception();
            }
            finally
            {
                <warning descr="'throw' inside 'finally' block">throw</warning> new Exception();
            }
        }
    }

    public void safe() throws IOException {
        try (FileInputStream in = new FileInputStream("name")) {

        } catch (RuntimeException e) {
            // ...
        } finally {
            try {
                throw new NullPointerException();
            } catch (RuntimeException e) {}
        }
    }

    interface BaseStream extends AutoCloseable {
        @Override
        void close();
    }

    static Runnable composedClose(BaseStream a, BaseStream b) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    a.close();
                }
                catch (Error | RuntimeException e1) {
                    try {
                        b.close();
                    }
                    catch (Error | RuntimeException e2) {
                        e1.addSuppressed(e2);
                    }
                    finally {
                        throw e1;
                    }
                }
                b.close();
            }
        };
    }

  /**
   * adapted from http://www.oracle.com/technetwork/articles/java/trywithresources-401775.html
   */
    public static void runWithoutMasking(FileInputStream a) throws IOException {
        IOException myException = null;
        try {
            a.read();
        } catch (IOException e) {
            myException = e;
            throw e;
        } finally {
            if (myException != null) {
                try {
                    a.close();
                } catch (Throwable t) {
                    myException.addSuppressed(t);
                }
            } else {
                a.close();
            }
        }
    }
}
