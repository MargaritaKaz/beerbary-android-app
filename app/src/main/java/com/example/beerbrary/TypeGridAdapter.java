package com.example.beerbrary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TypeGridAdapter extends BaseAdapter {
    private Context mContext;

    public Integer[] mThumbIds = {
            R.drawable.light, R.drawable.dark, R.drawable.ipa,
            R.drawable.apa, R.drawable.ale, R.drawable.stout,
            R.drawable.radler, R.drawable.porter, R.drawable.shandy
    };

    public String[] mThumbNames = {
            "Light Beer", "Dark Beer", "IPA", "APA", "Ale", "Stout", "Radler", "Porter", "Shandy"
    };

    public TypeGridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.type_grid_layout, null);
            ImageView imageView = (ImageView) grid.findViewById(R.id.gridTypeImage);
            TextView textView = (TextView) grid.findViewById(R.id.gridTypeText);
            imageView.setImageResource(mThumbIds[position]);
            textView.setText(mThumbNames[position]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BeerTypeListActivity.class);
                    intent.putExtra("FindAll", false);
                    intent.putExtra("Id", Integer.toString(position+1));
                    intent.putExtra("Name", mThumbNames[position]);
                    view.getContext().startActivity(intent);
                }
            });
        }
        else
        {
            grid = (View) convertView;
        }


        return grid;
    }
}
