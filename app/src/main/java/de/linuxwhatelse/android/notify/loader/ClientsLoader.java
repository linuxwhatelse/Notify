package de.linuxwhatelse.android.notify.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;

/**
 * Created by tadly on 12/10/14 at 10:39 AM.
 */
public class ClientsLoader extends AsyncTaskLoader<List<Client>> {
    Context context;
    List<Client> mModels;

    public ClientsLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<Client> loadInBackground() {
        ClientsDataSource dataSource = new ClientsDataSource(context);
        List<Client> clients = dataSource.getAllClients();
        dataSource.close();

        return clients;
    }

    @Override
    public void deliverResult(List<Client> listOfData) {
        if (isReset()) {
            if (listOfData != null) {
                onReleaseResources(listOfData);
            }
        }
        List<Client> oldData = listOfData;
        mModels = listOfData;

        if (isStarted()) {
            super.deliverResult(listOfData);
        }

        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mModels != null) {
            deliverResult(mModels);
        }

        if (takeContentChanged() || mModels == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<Client> data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mModels != null) {
            onReleaseResources(mModels);
            mModels = null;
        }
    }

    protected void onReleaseResources(List<Client> data) {
    }
}
