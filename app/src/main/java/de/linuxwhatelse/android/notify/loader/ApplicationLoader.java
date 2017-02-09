package de.linuxwhatelse.android.notify.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.linuxwhatelse.android.notify.database.ApplicationsDataSource;
import de.linuxwhatelse.android.notify.models.Application;

/**
 * Created by tadly on 12/10/14 at 10:39 AM.
 */
public class ApplicationLoader extends AsyncTaskLoader<List<Application>> {
    Context context;
    List<Application> mModels;

    int clientId = -1;

    public ApplicationLoader(Context context, int clientId) {
        super(context);
        this.context = context;
        this.clientId = clientId;
    }

    @Override
    public List<Application> loadInBackground() {
        ApplicationsDataSource dataSource = new ApplicationsDataSource(context);
        ArrayList<String> activeApps = dataSource.getAllPackageNamesForClient(clientId);
        dataSource.close();

        List<ApplicationInfo> allApps = getContext().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        List<Application> applications = new ArrayList<Application>();
        Application tmpApplication;
        for (ApplicationInfo appInfo : allApps) {
            if (appInfo.enabled) {
                tmpApplication = new Application();
                tmpApplication.setAppName(appInfo.loadLabel(context.getPackageManager()).toString());
                tmpApplication.setAppPackage(appInfo.packageName);
                tmpApplication.setAppIcon(appInfo.loadIcon(context.getPackageManager()));
                tmpApplication.setActivated(false);

                if (activeApps.contains(appInfo.packageName))
                    tmpApplication.setActivated(true);


                applications.add(tmpApplication);
            }
        }

        Collections.sort(applications, new Comparator<Application>() {
            public int compare(Application v1, Application v2) {
                return v1.getAppName().toLowerCase().compareTo(v2.getAppName().toLowerCase());
            }
        });

        return applications;
    }

    @Override
    public void deliverResult(List<Application> listOfData) {
        if (isReset()) {
            if (listOfData != null) {
                onReleaseResources(listOfData);
            }
        }
        List<Application> oldData = listOfData;
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
    public void onCanceled(List<Application> data) {
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

    protected void onReleaseResources(List<Application> data) {
    }
}
