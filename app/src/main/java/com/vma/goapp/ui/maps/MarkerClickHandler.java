package com.vma.goapp.ui.maps;

import android.content.Context;
import android.util.Log;

import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.util.ListenableList;
import com.vma.goapp.libs.DistanceUnit;

public class MarkerClickHandler {

    public MarkerClickHandler(Context context, ListenableList<Graphic> graphicList, Point tapPoint, int mSelectScale, OnActionListener onActionListener){
        double pClickSize = 0;
        int position = -1;
        int i = 0;
        int indexPoint = 0;
        int indexSelect = 0;
        for (Graphic graphic : graphicList){
            if (graphic.getGeometry().getGeometryType() == GeometryType.POINT){
                Point x  = (Point) graphic.getGeometry();

                DistanceUnit distanceUnit = new DistanceUnit(context);
                distanceUnit.calcDistance(x, tapPoint);
                double distance = distanceUnit.getNm();
                double distanceMetre = distance * 1852;
                Log.d("TAGRZ","DATA "+ x +" --> "+ distanceMetre);
                if (pClickSize == 0){
                    pClickSize = distanceMetre;
                    position = i;
                    indexSelect = indexPoint;
                }
                else {
                    if (pClickSize > distanceMetre){
                        pClickSize = distanceMetre;
                        position = i;
                        indexSelect = indexPoint;
                    }
                }
                indexPoint ++;
            }
            i++;
        }
        if (position == -1){
            return;
        }

        if (mSelectScale >8 && pClickSize < 15000){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
        else  if (mSelectScale >=6 && pClickSize < 2500){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
        else  if (mSelectScale >=4 && pClickSize < 1250){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
        else  if (mSelectScale >=2 && pClickSize < 320){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
        else  if (mSelectScale ==1 && pClickSize < 90){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
        else  if (mSelectScale ==0 && pClickSize < 50){
            if (onActionListener != null){
                onActionListener.onAction(position,indexSelect);
            }
        }
    }


    public interface OnActionListener{
        void onAction(int index, int indexPoint);
    }
}
