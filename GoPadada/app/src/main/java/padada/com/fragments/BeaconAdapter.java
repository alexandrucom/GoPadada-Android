package padada.com.fragments;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.Eddystone;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

import padada.com.R;

class BeaconAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Beacon> mBeaconList = new ArrayList<Beacon>();

    public BeaconAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBeaconList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBeaconList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mBeaconList.get(i).getBluetoothAddress().hashCode();
    }

    public void setBeaconList(List<Beacon> beacons) {
        android.util.Log.d("Beacon", "Received beacons " + beacons);
        ArrayList<Beacon> newBeacons = new ArrayList<>();
        if (beacons != null) {
            for (Beacon beacon : beacons) {
                int beaconPos = containsBeacon(mBeaconList, beacon);
                //Log.d("Beacon", "Beacon pos is " + beaconPos);
                if (beaconPos < 0) {
                    //Log.d("Beacon", "Beacon is not in the list add it");
                    newBeacons.add(beacon);
                } else {
                    //Log.d("Beacon", "Beacon is already in list");
                    Beacon beaconFromList = mBeaconList.get(beaconPos);
                    //Log.d("Beacon", "Beacon from list is " + beaconFromList);
                    if (beaconFromList instanceof IBeacon) {
                        //Log.d("Beacon", "Beacon is iBeacon");
                        if (beacon instanceof IBeacon) {
                            IBeacon newIBeacon = (IBeacon) beacon;
                            mBeaconList.set(beaconPos, newIBeacon);
                        } else {
                            android.util.Log.e("Beacon", "Update IBeacon failed. Difference between objects");
                        }
                    } else if (beaconFromList instanceof Eddystone) {
                        //Log.d("Beacon", "Beacon is iBeacon");
                        Eddystone oldEddystone = (Eddystone) beaconFromList;
                        if (beacon instanceof Eddystone) {
                            //Log.d("Beacon", "Update eddystone");
                            Eddystone newEddystone = (Eddystone) beacon;
                            oldEddystone.setNamespaceId(newEddystone.getNamespaceId() != null ?
                                    newEddystone.getNamespaceId() : oldEddystone.getNamespaceId());
                            oldEddystone.setInstanceId(newEddystone.getInstanceId() != null ?
                                    newEddystone.getInstanceId() : oldEddystone.getInstanceId());
                            oldEddystone.setFrameType(newEddystone.getFrameType());
                            oldEddystone.setRssi(newEddystone.getRssi());
                            oldEddystone.setTxPower(newEddystone.getTxPower());
                            oldEddystone.setUrl(newEddystone.getUrl() != null ? newEddystone.getUrl() :
                                    oldEddystone.getUrl());
                        } else {
                            android.util.Log.e("Beacon", "Update eddystone not possible. Difference between objects");
                        }

                    }
                }
            }
        }

        android.util.Log.d("AllList", "List with all the beacons " + mBeaconList);

        mBeaconList.addAll(newBeacons);
        notifyDataSetChanged();
    }

    public int containsBeacon(ArrayList<Beacon> beacons,
                              Beacon beacon) {
        int k = -1;
        for (int i=0; i < beacons.size(); ++i) {
            Beacon b = beacons.get(i);
            if (b.getBluetoothAddress().toLowerCase()
                    .equals(beacon.getBluetoothAddress().toLowerCase())) {
                k = i;
            }
        }
        return k;
    }

    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.item_beacon, parent, false);
            holder = new ViewHolder();
            holder.beaconName = (TextView) rowView.findViewById(R.id.beacon_name);
            holder.beaconIcon = (ImageView) rowView.findViewById(R.id.beacon_icon);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        Beacon beacon = mBeaconList.get(position);
        String proximity = "immediate";
        if (beacon.getProximity() == Eddystone.PROXIMITY_IMMEDIATE) {
            proximity = "immediate";
        } else if (beacon.getProximity() == Eddystone.PROXIMITY_NEAR) {
            proximity = "near";
        } else if (beacon.getProximity() == Eddystone.PROXIMITY_FAR) {
            proximity = "far";
        } else {
            proximity = "unknown";
        }
        if (beacon instanceof IBeacon) {
            IBeacon iBeacon = (IBeacon) beacon;
            holder.beaconName.setText(Html.fromHtml("<b>Major:</b> " + iBeacon.getMajor() + "<br><b>Minor:</b> " +
                    iBeacon.getMinor() + " <br><b>RSSI:</b> " + iBeacon.getRssi() +
                    "<br><b>Proximity:</b> " + proximity));
            holder.beaconIcon.setImageResource(R.mipmap.ic_ibeacon_vector);
        } else if (beacon instanceof Eddystone) {
            Eddystone eddystone = (Eddystone) beacon;
            android.util.Log.d("DisplayCoupon", "Eddystone toString " + eddystone.toString());
            if (eddystone.getFrameType() == Eddystone.UUID_FRAME) {
                String eddystoneDetails = "<b>Namespace:</b> " + eddystone.getNamespaceId() +
                        " <br><b>Instance:</b> " + eddystone.getInstanceId() + " <br><b>RSSI:</b> " + eddystone.getRssi() +
                        "<br><b>Proximity:</b> " + proximity;
                if (eddystone.getUrl() != null) {
                    eddystoneDetails += "<br> <b>URL: </b> " + eddystone.getUrl();
                }
                holder.beaconName.setText(Html.fromHtml(eddystoneDetails));

            } else if (eddystone.getFrameType() == Eddystone.URL_FRAME) {
                if (eddystone.getNamespaceId() != null) {
                    String eddystoneDetails = "<b>Namespace:</b> " + eddystone.getNamespaceId() +
                            " <br><b>Instance:</b> " + eddystone.getInstanceId() + " <br><b>RSSI:</b> " + eddystone.getRssi() +
                            "<br><b>Proximity:</b> " + proximity + "<br><b>URL: </b> " +
                            eddystone.getUrl();
                    holder.beaconName.setText(Html.fromHtml(eddystoneDetails));
                } else {
                    holder.beaconName.setText(Html.fromHtml("<b>URL:</b> " +
                            eddystone.getUrl() + " <br><b>RSSI:</b> " + eddystone.getRssi() +
                            "<br><b>Proximity:</b> " + proximity));
                }
            }
            holder.beaconIcon.setImageResource(R.mipmap.ic_launcher);
        }
        return rowView;
    }

    public static class ViewHolder {
        TextView beaconName;
        ImageView beaconIcon;
    }
}