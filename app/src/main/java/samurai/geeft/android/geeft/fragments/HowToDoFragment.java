package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baasbox.android.BaasUser;
import com.squareup.picasso.Picasso;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 18/02/16.
 */
public class HowToDoFragment extends StatedFragment{
    private ImageView mImageView;
    private ImageView mImageView2;
    private TextView mTextView;
    private TextView mTextView2;


    public static HowToDoFragment newInstance(Bundle b) {
        HowToDoFragment fragment = new HowToDoFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_how_to_do, container, false);
        mImageView = (ImageView)rootView.findViewById(R.id.how_to_do_photo_frame);
        mTextView = (TextView)rootView.findViewById(R.id.how_to_do_step1);
        Picasso.with(getContext()).load("http://"+TagsValue.API_DOMAIN+":"+TagsValue.APP_PORT+"/file/" +
                "b31b7dcb-10f2-486e-9f79-b2c5f44cd5ab?X-BAASBOX-APPCODE1234567890&X-BB-SESSION=" +
                        "X-BAASBOX-APPCODE1234567890&X-BB-SESSION="+ BaasUser.current().getToken())
                .fit().centerInside()
                .into(mImageView);
        mTextView.setText("Il tavolo è formato da un piano orizzontale di legno, " +
                "metallo, plastica o altro materiale rigido sostenuto da due, tre, " +
                "quattro o più gambe, di forma e dimensioni diverse a seconda dell'uso a " +
                "cui è adibito. Può anche essere sostenuto da una colonna centrale, " +
                "in questo caso di solito ha un aspetto più elegante e una superficie " +
                "più limitata e spesso circolare.");

        return rootView;
    }


}
