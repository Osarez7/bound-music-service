package co.edu.intecap.boundservice.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.intecap.boundservice.R;
import co.edu.intecap.boundservice.model.Song;

public class AdapterClass extends ArrayAdapter<Song> {
    Context cxt;
    int res;
    ArrayList<Song> list;

    public AdapterClass(Context context, int resource, ArrayList<Song> objects) {
        super(context, resource, objects);
        cxt=context;
        res=resource;
        list=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;

        //Initializing view which will point to layout file list_item
        view= LayoutInflater.from(cxt).inflate(res,parent,false);

        //Initializing TextView
        TextView fileName= view.findViewById(R.id.textSong);

        Song sdOb=list.get(position);
        //Setting the Icon and FileName
        fileName.setText(sdOb.getFileName());

        return view;
    }


}
