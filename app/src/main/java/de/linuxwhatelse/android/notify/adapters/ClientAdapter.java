package de.linuxwhatelse.android.notify.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;

/**
 * Created by tadly on 12/11/14.
 */
public class ClientAdapter extends ArrayAdapter<Client> {
    private final Activity context;

    public ClientAdapter(Activity context) {
        super(context, R.layout.client_listiew_row);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.client_listiew_row, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.clientName = rowView.findViewById(R.id.client_listview_row_name);
            viewHolder.clientHost = rowView.findViewById(R.id.client_listview_row_host);
            viewHolder.activate = rowView.findViewById(R.id.client_listview_row_activate);
            viewHolder.activate.setOnClickListener(onClientActiveStateClickListener);
            rowView.setTag(viewHolder);
        }


        Client data = getItem(position);

        ViewHolder holder = (ViewHolder) rowView.getTag();

        if (data != null) {
            if (data.getName() == null || data.getName().equals(""))
                holder.clientName.setText(data.getHost());
            else
                holder.clientName.setText(data.getName());

            holder.clientHost.setText(data.getHost() + ":" + data.getPort());
            holder.activate.setChecked(data.isActive());
            holder.activate.setTag(data);
        }

        return rowView;
    }

    public void setData(List<Client> clients) {
        clear();

        if (clients != null) {
            for (Client entry : clients) {
                add(entry);
            }
        }
    }

    public void updateItem(Client client) {
        int pos = getPosition(client);
        remove(client);
        insert(client, pos);
    }

    private static class ViewHolder {
        public TextView clientName;
        public TextView clientHost;
        public Switch activate;
    }

    View.OnClickListener onClientActiveStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClientsDataSource dataSource = new ClientsDataSource(context);
            Client client = (Client) v.getTag();
            client.setActive(((Switch) v).isChecked());

            dataSource.updateClient(client);
        }
    };

}