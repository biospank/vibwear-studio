package it.vibwear.app.adapters;

import java.util.ArrayList;

import it.lampwireless.vibwear.app.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServicesAdapter extends ArrayAdapter<ServiceItem> {
	private final Activity activity;
	private final ArrayList<ServiceItem> values;

	// our ViewHolder.
	// caches our TextView
//	static class ViewHolderItem {
//	    TextView textViewItem;
//	    ImageView iconViewItem;
//	}

	public ServicesAdapter(Activity activity, ArrayList<ServiceItem> values) {
		super(activity.getApplicationContext(), R.layout.service_row, values);
		this.activity = activity;
		this.values = values;
	}
	  
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

   	    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   	    View rowView = inflater.inflate(R.layout.service_row, parent, false);
	    
   	    TextView textViewItem = (TextView) rowView.findViewById(R.id.label);
   	    ImageView iconViewItem = (ImageView) rowView.findViewById(R.id.icon);
		    
		ServiceItem service = values.get(position);
	 
		service.setTextView(textViewItem);
		service.setIconView(iconViewItem);

	    return rowView;
	}
}
