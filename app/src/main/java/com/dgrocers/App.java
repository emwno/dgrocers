package com.dgrocers;

import android.app.Application;

import com.dgrocers.firebase.AccountManager;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.firebase.LocalStorageManager;
import com.dgrocers.services.OrderService;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LocalStorageManager.init(this);
		FirebaseManager.getInstance();
		AccountManager.getInstance();
		OrderService.getInstance();

//		{
//			Location l = new Location();
//			l.setName("DHA2");
//			Area a1 = new Area();
//			a1.setName("Askari Towers II");
//			a1.addSubArea("Block 1");
//			a1.addSubArea("Block 2");
//			a1.addSubArea("Block 3");
//			a1.addSubArea("Block 4");
//			a1.addSubArea("Block 5");
//			a1.addSubArea("Block 6");
//			a1.addSubArea("Block 7");
//			a1.addSubArea("Block 8");
//			a1.addSubArea("Block 9");
//			a1.addSubArea("Block 10");
//			a1.addSubArea("Block 11");
//			a1.addSubArea("Block 12");
//
//
//			Area aD = new Area();
//			aD.setName("Defence Residency");
//			Area aL = new Area();
//			aL.setName("Lignum Towers");
//
//			Area a2a = new Area();
//			a2a.setName("Sector A");
//			Area a2b = new Area();
//			a2b.setName("Sector B");
//			Area a2c = new Area();
//			a2c.setName("Sector C");
//			Area a2d = new Area();
//			a2d.setName("Sector D");
//			Area a2e = new Area();
//			a2e.setName("Sector E");
//			Area a2f = new Area();
//			a2f.setName("Sector F");
//			Area a2g = new Area();
//			a2g.setName("Sector G");
//			Area a2h = new Area();
//			a2h.setName("Sector H");
//			Area a2j = new Area();
//			a2j.setName("Sector J");
//			l.addArea(a1);
//			l.addArea(aD);
//			l.addArea(aL);
//			l.addArea(a2a);
//			l.addArea(a2b);
//			l.addArea(a2c);
//			l.addArea(a2d);
//			l.addArea(a2e);
//			l.addArea(a2f);
//			l.addArea(a2g);
//			l.addArea(a2h);
//			l.addArea(a2j);
//			FirebaseManager.getInstance().mLocationCollectionRef.document("DHA2").set(l);
//		}
	}

}
