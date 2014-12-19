package org.dronix.dmesg.task;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class DmesgTask extends BaseAsyncLoaderTask<String> {

    private Handler mHandler;
    private static final String TAG ="DmesgTask";
    public DmesgTask(Context context, Handler handler) {
        super(context);
        mHandler = handler;

    }

    @Override
    public String loadInBackground() {
        int totLen = 0;
        String line = "";

        while (!isStopped()) {

            line = execRoot("dmesg");
            totLen = line.length();
            if (totLen > 10000)
                line = line.substring(totLen - 10000, totLen);

            sendMessage(line);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            RootTools.closeShell(true);
        } catch (IOException e) {
           Log.e(TAG, "close shell :" + e.getMessage());
        }

        return null;
    }

    public String execRoot(String cmd) {
        CommandCapture command = new CommandCapture(0, cmd);
        try {
            Shell shell = RootTools.getShell(true);
            shell.add(command).waitForFinish();
            shell.close();
            shell = null;
        } catch (IOException e) {
            Log.e(TAG, "exec Root :" + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, "exec Root :" + e.getMessage());
        } catch (RootDeniedException e) {
            Log.e(TAG, "exec Root :" + e.getMessage());
        } catch (TimeoutException e) {
            Log.e(TAG, "exec Root :" + e.getMessage());
        }

        return command.toString();
    }

    private void sendMessage(String message) {
        if (message.length() > 0) {
            Message msg = Message.obtain();
            msg.obj = message;
            mHandler.sendMessage(msg);
        }
    }


    private String exec(String command) {

        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
        } catch (IOException e) {
            Log.e(TAG, "exec :" + e.getMessage());

        } catch (InterruptedException e) {
            Log.e(TAG, "exec :" + e.getMessage());

        }
        return output.toString();
    }
}
