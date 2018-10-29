package no.hiof.fridgebro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

import no.hiof.fridgebro.R;
import no.hiof.fridgebro.adapters.ContextMenuAdapter;
import no.hiof.fridgebro.models.Item;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContextMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContextMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContextMenuFragment extends DialogFragment {

    private ArrayList<Item> queryResult = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContextMenuAdapter contextMenuAdapter;

    private OnFragmentInteractionListener mListener;

    public ContextMenuFragment() {
        // Required empty public constructor
    }

    public ArrayList<Item> getQueryResult() {
        return queryResult;
    }

    public ContextMenuAdapter getContextMenuAdapter() {
        return contextMenuAdapter;
    }

    public static ContextMenuFragment newInstance(ArrayList<Item> queryResult) {
        ContextMenuFragment cmFrag = new ContextMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("queryResult", queryResult);
        cmFrag.setArguments(bundle);
        return cmFrag;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContextMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*
    public static ContextMenuFragment newInstance(String param1, String param2) {
        ContextMenuFragment fragment = new ContextMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryResult = getArguments().getParcelableArrayList("queryResult");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_context_menu, container, false);
        recyclerView = v.findViewById(R.id.rcvContext);
        contextMenuAdapter = new ContextMenuAdapter(queryResult, getContext(), this);
        recyclerView.setAdapter(contextMenuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    private void getImageBitmaps(){
        queryResult.add(new Item("Test", "Test 1", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 2", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 3", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 4", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 5", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 6", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 7", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
        queryResult.add(new Item("Test", "Test 8", "1", "11111", "https://i.redd.it/oir304dowbs11.jpg", "03/03/2019"));
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
