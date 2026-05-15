package com.xdel.luke.objClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Pinger {
    private Runtime runtime;
    private Thread pingThread;
    private OnPingListener onPingListener;

    public Pinger() {
        runtime = Runtime.getRuntime();
    }

    public void setOnPingListener(OnPingListener onPingListener) {
        this.onPingListener = onPingListener;
    }

    public String ping2(String destination, int timeoutInSeconds) throws InterruptedException {
        String res = "";
        try {
            String command = String.format("/system/bin/ping -c 5 -W %d %s", timeoutInSeconds, destination);
            Process process = runtime.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String s;
            ArrayList sa = new ArrayList();
            while ((s = stdInput.readLine()) != null) {
                sa.add(s);
                //res += s + "\n";
            }
            int c = 0;
            for (int i = sa.size() -1; i >= 0; i--) {
                if (c == 2) {
                    break;
                } else {
                    res += sa.get(i) + "\n";
                    c++;
                }
            }
            process.destroy();

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at Pinger ping. Exception: " + s,"","","","");
        } finally {
            return res;
        }
    }

    public boolean ping(String destination, int timeoutInSeconds) throws InterruptedException {
        try {
            String command = String.format("/system/bin/ping -c 5 -W %d %s", timeoutInSeconds, destination);
            Process process = runtime.exec(command);
            int ret = process.waitFor();
            process.destroy();
            return ret == 0;
        } catch (IOException e) {
            //e.printStackTrace();
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at Pinger ping. Exception: " + s,"","","","");
        }
        return false;
    }

    public void pingUntilSucceeded(final String destination, final long intervalInMillis) {
        pingThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (true) {
                        if (ping(destination, 3)) {
                            if (onPingListener != null)
                                onPingListener.onPingSuccess();
                            break;
                        } else {
                            if (onPingListener != null)
                                onPingListener.onPingFailure();
                        }

                        Thread.sleep(intervalInMillis);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (onPingListener != null)
                    onPingListener.onPingFinish();
            }
        };
        pingThread.start();
    }

    public void pingUntilFailed(final String destination, final long intervalInMillis) {
        pingThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (true) {
                        if (!ping(destination, 3)) {
                            if (onPingListener != null)
                                onPingListener.onPingFailure();
                            break;
                        } else {
                            if (onPingListener != null)
                                onPingListener.onPingSuccess();
                        }

                        Thread.sleep(intervalInMillis);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (onPingListener != null)
                    onPingListener.onPingFinish();
            }
        };
        pingThread.start();
    }

    public void cancel() {
        if (pingThread != null)
            pingThread.interrupt();
    }

    public interface OnPingListener {
        void onPingSuccess();
        void onPingFailure();
        void onPingFinish();
    }


}
