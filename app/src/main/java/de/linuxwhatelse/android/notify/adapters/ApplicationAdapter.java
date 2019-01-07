package de.linuxwhatelse.android.notify.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.database.ApplicationsDataSource;
import de.linuxwhatelse.android.notify.models.Application;

/**
 * Created by tadly on 12/11/14.
 */
public class ApplicationAdapter extends ArrayAdapter<Application> implements Filterable {
    private final Activity context;
    protected int clientId = -1;

    private List<Application> items;

    public ApplicationAdapter(Activity context) {
        super(context, R.layout.apps_listiew_row);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.apps_listiew_row, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.appIcon = rowView.findViewById(R.id.application_listview_row_icon);
            viewHolder.appName = rowView.findViewById(R.id.application_listview_row_name);
            viewHolder.active = rowView.findViewById(R.id.application_listview_row_active);
            viewHolder.active.setOnClickListener(onAppActiveStateClickListener);

            rowView.setTag(viewHolder);
        }

        Application data = getItem(position);

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.appName.setText(data.getAppName());
        holder.appIcon.setImageDrawable(data.getAppIcon());
        holder.active.setChecked(data.isActivated());
        holder.active.setTag(data);

        return rowView;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public List<Application> getData() {
        return this.items;
    }

    public void setData(List<Application> applications) {
        clear();
        items = applications;
        if (applications != null) {
            for (Application entry : applications) {
                add(entry);
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    clear();
                } else {
                    clear();
                    for (Application app : (List<Application>) results.values) {
                        add(app);
                    }
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint.equals("")) {
                    results.values = getData();
                    results.count = getData().size();
                } else {
                    List<Application> apps = new ArrayList<Application>();
                    for (Application app : getData()) {
                        if (app.getAppName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            apps.add(app);
                        }
                    }

                    results.values = apps;
                    results.count = apps.size();
                }

                return results;
            }
        };
    }

    private static class ViewHolder {
        ImageView appIcon;
        TextView appName;
        Switch active;
    }

    View.OnClickListener onAppActiveStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean active = ((Switch) v).isChecked();

            ApplicationsDataSource dataSource = new ApplicationsDataSource(context);
            Application app = (Application) v.getTag();
            app.setActivated(active);

            if (active) {
                dataSource.addApplication(clientId, app.getAppPackage());
            } else {
                dataSource.removeApplication(clientId, app.getAppPackage());
            }
        }
    };
}