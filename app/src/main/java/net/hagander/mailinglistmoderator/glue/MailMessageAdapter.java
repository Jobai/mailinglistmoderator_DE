/*
 * MailMessageAdapter.java - This class holds an ArrayAdapter for the QueueListActivity activity.
 * 
 * Copyright (C) 2010 Magnus Hagander <magnus@hagander.net>
 * 
 * This software is released under the BSD license.
 */
package net.hagander.mailinglistmoderator.glue;

import java.util.Vector;

import net.hagander.mailinglistmoderator.R;
import net.hagander.mailinglistmoderator.backend.MailMessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Magnus Hagander <magnus@hagander.net>
 */
public class MailMessageAdapter extends ArrayAdapter<MailMessage> {

    public static  Vector<String> blacklist = new Vector<String>();
    private Vector<MailMessage> items;

    private Bitmap img_green = null, img_red = null, img_orange = null, img_spam = null;

    public MailMessageAdapter(Context context, int textViewResourceId,
                              Vector<MailMessage> objects) {
        super(context, textViewResourceId, objects);

        items = objects;

        try {
            img_green = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.green);
            img_red = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.red);
            img_orange = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.orange);
            img_spam = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.spam);
        } catch (Exception e) {
            Log.w("MailMessageAdapter", String.format(
                    "Exception loading images: %s", e.getMessage()));
        }
    }

    /**
     * Create a view that contains the information we want to show about each
     * message, including an image representing the status.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.mail_item, null);
        }
        MailMessage o = items.get(position);
        if (o != null) {

            o.checkForSpam(MailMessageAdapter.blacklist);

            TextView sender = (TextView) v.findViewById(R.id.TextView_Sender);
            TextView subj = (TextView) v.findViewById(R.id.TextView_Subject);
            ImageView i = (ImageView) v.findViewById(R.id.ImageViewAction);

            sender.setText(o.getSender());
            subj.setText(o.getSubject());
            try {
                switch (o.getStatus()) {
                    case Accept:
                        i.setImageBitmap(img_green);
                        break;
                    case Reject:
                        if(o.isSpam())
                        {
                            i.setImageBitmap(img_spam);
                        }
                        else
                        {
                            i.setImageBitmap(img_red);
                        }
                        break;
                    case Denied:
                        i.setImageBitmap(img_orange); //FIXME change to different icon
                        break;
                    case Defer:
                        i.setImageBitmap(null);
                        break;
                }
            } catch (Exception e) {
                Log.w("getView", String.format("Meh, got exception: %s", e
                        .getMessage()));
            }
        }
        return v;
    }
}
