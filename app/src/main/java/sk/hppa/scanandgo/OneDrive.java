package sk.hppa.scanandgo;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.onedrive.sdk.authentication.ADALAuthenticator;
import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;

import java.util.concurrent.atomic.AtomicReference;


public class OneDrive extends Activity {

    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<>();

    private IClientConfig createConfig() {
        final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
            @Override
            public String getClientId() {
                return "c4d49a90-0c97-4868-a93d-bc1bcbb9b75b";
            }

            @Override
            public String[] getScopes() {
                return new String[] { "onedrive.appfolder", "onedrive.readwrite" };
            }
        };
        final ADALAuthenticator adalAuthenticator = new ADALAuthenticator() {
            @Override
            public String getClientId() {
                return "c4d49a90-0c97-4868-a93d-bc1bcbb9b75b";
            }

            @Override
            protected String getRedirectUrl() {
                return "https://localhost";
            }
        };
        final IClientConfig config = DefaultClientConfig.createWithAuthenticators(
                msaAuthenticator,
                adalAuthenticator);

        return config;
    }

    void signOut() {
        if (mClient.get() == null) {
            return;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                mClient.set(null);
                final Intent intent = new Intent(getBaseContext(), OneDrive.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failure(final ClientException ex) {
                Toast.makeText(getBaseContext(), "Logout error " + ex, Toast.LENGTH_LONG).show();
            }
        });
    }

    synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    synchronized void createOneDriveClient(final Activity activity) {
        final DefaultCallback<IOneDriveClient> callback = new DefaultCallback<IOneDriveClient>(activity) {
            @Override
            public void success(final IOneDriveClient result) {
                mClient.set(result);
            }

            @Override
            public void failure(final ClientException error) {
            }
        };

        new OneDriveClient
                .Builder()
                .fromConfig(createConfig())
                .loginAndBuildClient(activity, callback);
    }

}