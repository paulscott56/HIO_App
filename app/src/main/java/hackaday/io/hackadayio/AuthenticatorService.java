package hackaday.io.hackadayio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by paul on 2015/07/10.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}