package sk.hppa.scanandgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;

/**
 * A default callback that logs errors
 * @param <T> The type returned by this callback
 */
public class DefaultCallback<T> implements ICallback<T> {

    /**
     * The exception text for not implemented runtime exceptions
     */
    private static final String SUCCESS_MUST_BE_IMPLEMENTED = "Success must be implemented";

    /**
     * The context used for displaying toast notifications
     */
    private final Context mContext;

    /**
     * Default constructor
     * @param context The context used for displaying toast notifications
     */
    public DefaultCallback(final Context context) {
        mContext = context;
    }
    @Override
    public void success(final T t) {
        throw new RuntimeException(SUCCESS_MUST_BE_IMPLEMENTED);
    }

    @Override
    public void failure(final ClientException error) {
        if (error != null) {
            Log.e(getClass().getSimpleName(), error.getMessage());
            new AlertDialog
                    .Builder(mContext)
                    .setTitle("Error")
                    .setMessage(error.getMessage())
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }
}