package com.ioter.hopeland.uhf.UHF1Function;

import android.view.View;
import android.widget.RadioGroup;

public class commfun {
	public static  int SortGroup(RadioGroup rg)
	{
		 int check1=rg.getCheckedRadioButtonId();
		    if(check1!=-1)
		    {
		    	for(int i=0;i<rg.getChildCount();i++)
		    	{ 
		    	  View vi=rg.getChildAt(i);
		    	  int vv=vi.getId();
		    	  if(check1==vv)
		    	  {
		    		  return i;
		    	  }
		    	}
		    	
		    	return -1;
		    }
		    else
		    	return check1;
	}
}
