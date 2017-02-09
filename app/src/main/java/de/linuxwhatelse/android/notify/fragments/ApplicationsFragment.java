package de.linuxwhatelse.android.notify.fragments;

import android.animation.LayoutTransition;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.adapters.ApplicationAdapter;
import de.linuxwhatelse.android.notify.loader.ApplicationLoader;
import de.linuxwhatelse.android.notify.models.Application;

/**
 * Created by tadly on 12/11/14.
 */

public class ApplicationsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Application>> {
    ApplicationsFragment context;
    ApplicationAdapter adapter;

    SearchView applicationSearchView;

    private int clientId = -1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            this.clientId = getArguments().getInt("client_id", -1);
        }

        this.context = this;

        setHasOptionsMenu(true);

        this.adapter = new ApplicationAdapter(getActivity());
        this.adapter.setClientId(clientId);


        setListAdapter(adapter);

        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.application_toolbar_menu, menu);

        this.applicationSearchView = (SearchView) menu.findItem(R.id.application_toolbar_searchview).getActionView();
        this.applicationSearchView.setOnQueryTextListener(onSearchQueryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, final View v, int position, long id) {
        //ToDo: Separate activity to configure things like displaytime etc.
    }

    @Override
    public Loader<List<Application>> onCreateLoader(int id, Bundle args) {
        return new ApplicationLoader(getActivity(), this.clientId);
    }

    @Override
    public void onLoadFinished(Loader<List<Application>> loader, List<Application> data) {
        adapter.setData(data);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Application>> loader) {
        adapter.setData(null);
    }


    SearchView.OnQueryTextListener onSearchQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            adapter.getFilter().filter(s);
            return false;
        }
    };
}
