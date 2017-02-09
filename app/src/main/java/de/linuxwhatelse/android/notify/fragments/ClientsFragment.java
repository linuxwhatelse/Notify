package de.linuxwhatelse.android.notify.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.activities.ClientPreferenceActivity;
import de.linuxwhatelse.android.notify.activities.QRCodeScannerActivity;
import de.linuxwhatelse.android.notify.adapters.ClientAdapter;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.loader.ClientsLoader;
import de.linuxwhatelse.android.notify.models.Client;

/**
 * Created by tadly on 12/11/14.
 */
public class ClientsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Client>> {
    public static final int REFRESH_CLIENTS_LIST = 1;
    public static final int QR_SCAN_RESULT = 3;

    ClientsFragment context;
    ClientAdapter adapter;

    FloatingActionMenu fabMenu;
    FloatingActionButton fabManual;
    FloatingActionButton fabQRCode;

    View.OnClickListener onAddDeviceManuallyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fabMenu.close(true);

            Client tmpClient = new Client();
            tmpClient.setActive(true);
            ClientsDataSource dataSource = new ClientsDataSource(getActivity());
            int clientId = dataSource.addClient(tmpClient);
            dataSource.close();

            Intent intent = new Intent(getActivity(), ClientPreferenceActivity.class);
            intent.putExtra("client_id", clientId);
            intent.putExtra("is_new_client", true);

            startActivityForResult(intent, REFRESH_CLIENTS_LIST);
        }
    };
    View.OnClickListener onAddDeviceViaQRCodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fabMenu.close(true);

            startActivityForResult(new Intent(getActivity(), QRCodeScannerActivity.class), QR_SCAN_RESULT);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.clients_listview, container, false);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (115 * scale + 0.5f);

        v.findViewById(android.R.id.list).setPadding(0, 0, 0, dpAsPixels);
        ((ListView) v.findViewById(android.R.id.list)).setClipToPadding(false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = this;

        if (getView() != null) {
            fabMenu = (FloatingActionMenu) getView().findViewById(R.id.clients_listview_fab);

            fabManual = (FloatingActionButton) getView().findViewById(R.id.clients_listview_fab_action_manually);
            fabManual.setOnClickListener(onAddDeviceManuallyClickListener);

            fabQRCode = (FloatingActionButton) getView().findViewById(R.id.clients_listview_fab_action_via_qr_code);
            fabQRCode.setOnClickListener(onAddDeviceViaQRCodeClickListener);
        }

        setHasOptionsMenu(true);
        registerForContextMenu(this.getListView());

        setEmptyText(getString(R.string.message_listview_clients_empty));

        this.adapter = new ClientAdapter(getActivity());
        setListAdapter(adapter);

        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.clients_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Client client = adapter.getItem(adapterContextMenuInfo.position);

        switch (item.getItemId()) {
            case R.id.devices_context_edit:
                Intent intent = new Intent(getActivity(), ClientPreferenceActivity.class);
                intent.putExtra("client_id", client.getId());

                startActivityForResult(intent, REFRESH_CLIENTS_LIST);
                break;

            case R.id.devices_context_delete:
                ClientsDataSource dataSource = new ClientsDataSource(getActivity());
                dataSource.removeClient(client.getId());
                dataSource.close();

                adapter.remove(client);
                break;
        }

        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), ClientPreferenceActivity.class);
        intent.putExtra("client_id", adapter.getItem(position).getId());

        startActivityForResult(intent, REFRESH_CLIENTS_LIST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REFRESH_CLIENTS_LIST:
                getLoaderManager().restartLoader(0, null, context);
                break;

            case QR_SCAN_RESULT:
                Client client = new Client();

                if (data == null)
                    return;

                String content = data.getStringExtra("content");
                try {
                    URI contentUri = new URI("http://" + content);
                    if (contentUri.getPort() != -1) {
                        client.setActive(true);
                        client.setHost(contentUri.getHost());
                        client.setPort(contentUri.getPort());
                        if (contentUri.getUserInfo() != null) {
                            String[] login = contentUri.getUserInfo().split(":");
                            if (login.length == 1) {
                                client.setUser(login[0]);
                            } else if (login.length == 2) {
                                client.setUser(login[0]);
                                client.setPwd(login[1]);
                            }
                        }

                        ClientsDataSource dataSource = new ClientsDataSource(getActivity());
                        client.setId(dataSource.addClient(client));
                        dataSource.close();

                        Intent intent = new Intent(getActivity(), ClientPreferenceActivity.class);
                        intent.putExtra("client_id", client.getId());

                        startActivityForResult(intent, REFRESH_CLIENTS_LIST);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.qr_scan_failed), Toast.LENGTH_SHORT).show();
                    }
                } catch (URISyntaxException e) {
                    Toast.makeText(getActivity(), getString(R.string.qr_scan_failed), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public Loader<List<Client>> onCreateLoader(int id, Bundle args) {
        return new ClientsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Client>> loader, List<Client> data) {
        adapter.setData(data);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Client>> loader) {
        adapter.setData(null);
    }
}
