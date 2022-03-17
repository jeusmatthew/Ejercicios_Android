package om.adivnum;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "om.adivnum", "om.adivnum.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "om.adivnum", "om.adivnum.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "om.adivnum.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtp1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtp2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmensajes = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnp1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnp2 = null;
public static byte _randomn = (byte)0;
public static byte _turn = (byte)0;
public anywheresoftware.b4a.objects.ButtonWrapper _btnplay = null;
public om.adivnum.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 40;BA.debugLine="Activity.LoadLayout(\"Layout\")";
mostCurrent._activity.LoadLayout("Layout",mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _btnp1_click() throws Exception{
int _inputplayer = 0;
 //BA.debugLineNum = 121;BA.debugLine="Private Sub btnP1_Click";
 //BA.debugLineNum = 122;BA.debugLine="Dim inputPlayer As Int";
_inputplayer = 0;
 //BA.debugLineNum = 124;BA.debugLine="inputPlayer = txtP1.Text";
_inputplayer = (int)(Double.parseDouble(mostCurrent._txtp1.getText()));
 //BA.debugLineNum = 126;BA.debugLine="CheckNum(inputPlayer, \"JUGADOR 1\")";
_checknum(_inputplayer,"JUGADOR 1");
 //BA.debugLineNum = 127;BA.debugLine="txtP1.Text = \"\"";
mostCurrent._txtp1.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public static String  _btnp2_click() throws Exception{
int _inputplayer = 0;
 //BA.debugLineNum = 130;BA.debugLine="Private Sub btnP2_Click";
 //BA.debugLineNum = 131;BA.debugLine="Dim inputPlayer As Int";
_inputplayer = 0;
 //BA.debugLineNum = 133;BA.debugLine="inputPlayer = txtP2.Text";
_inputplayer = (int)(Double.parseDouble(mostCurrent._txtp2.getText()));
 //BA.debugLineNum = 135;BA.debugLine="CheckNum(inputPlayer, \"JUGADOR 2\")";
_checknum(_inputplayer,"JUGADOR 2");
 //BA.debugLineNum = 136;BA.debugLine="txtP2.Text = \"\"";
mostCurrent._txtp2.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return "";
}
public static String  _btnplay_click() throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Private Sub btnPlay_Click";
 //BA.debugLineNum = 74;BA.debugLine="btnPlay.Visible = False";
mostCurrent._btnplay.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 75;BA.debugLine="P1Visible(True)";
_p1visible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 76;BA.debugLine="txtP1.RequestFocus";
mostCurrent._txtp1.RequestFocus();
 //BA.debugLineNum = 77;BA.debugLine="turn = 1";
_turn = (byte) (1);
 //BA.debugLineNum = 79;BA.debugLine="lblMensajes.Text = \"\"";
mostCurrent._lblmensajes.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 81;BA.debugLine="randomN = Rnd(1, 101)";
_randomn = (byte) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (101)));
 //BA.debugLineNum = 82;BA.debugLine="Log(randomN)";
anywheresoftware.b4a.keywords.Common.LogImpl("3983049",BA.NumberToString(_randomn),0);
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _checknum(int _num,String _pturnname) throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Private Sub CheckNum(num As Int, pTurnName As Stri";
 //BA.debugLineNum = 88;BA.debugLine="Log(pTurnName & \" - \" & num)";
anywheresoftware.b4a.keywords.Common.LogImpl("35767170",_pturnname+" - "+BA.NumberToString(_num),0);
 //BA.debugLineNum = 89;BA.debugLine="If num == randomN Then";
if (_num==_randomn) { 
 //BA.debugLineNum = 90;BA.debugLine="GameOver(pTurnName)";
_gameover(_pturnname);
 //BA.debugLineNum = 91;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 94;BA.debugLine="If num < randomN Then";
if (_num<_randomn) { 
 //BA.debugLineNum = 95;BA.debugLine="MsgboxAsync(\"El numero es mayor\", \"!!!\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("El numero es mayor"),BA.ObjectToCharSequence("!!!"),processBA);
 //BA.debugLineNum = 96;BA.debugLine="lblMensajes.Text = \"El numero es mayor\"";
mostCurrent._lblmensajes.setText(BA.ObjectToCharSequence("El numero es mayor"));
 }else if(_num>_randomn) { 
 //BA.debugLineNum = 99;BA.debugLine="MsgboxAsync(\"El numero es menor\", \"!!!\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("El numero es menor"),BA.ObjectToCharSequence("!!!"),processBA);
 //BA.debugLineNum = 100;BA.debugLine="lblMensajes.Text = \"El numero es menor\"";
mostCurrent._lblmensajes.setText(BA.ObjectToCharSequence("El numero es menor"));
 };
 //BA.debugLineNum = 103;BA.debugLine="turnChange";
_turnchange();
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
return "";
}
public static String  _gameover(String _pwinner) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Private Sub GameOver(pWinner As String)";
 //BA.debugLineNum = 53;BA.debugLine="MsgboxAsync(\"HAS GANADO \" & pWinner, \"GAME OVER\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("HAS GANADO "+_pwinner),BA.ObjectToCharSequence("GAME OVER"),processBA);
 //BA.debugLineNum = 54;BA.debugLine="lblMensajes.Text = \"GANADOR \" & pWinner";
mostCurrent._lblmensajes.setText(BA.ObjectToCharSequence("GANADOR "+_pwinner));
 //BA.debugLineNum = 55;BA.debugLine="P1Visible(False)";
_p1visible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 56;BA.debugLine="P2Visible(False)";
_p2visible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 57;BA.debugLine="btnPlay.Visible = True";
mostCurrent._btnplay.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private txtP1 As EditText";
mostCurrent._txtp1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private txtP2 As EditText";
mostCurrent._txtp2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private lblMensajes As Label";
mostCurrent._lblmensajes = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private lblP1 As Label";
mostCurrent._lblp1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private lblP2 As Label";
mostCurrent._lblp2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private btnP1 As Button";
mostCurrent._btnp1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private btnP2 As Button";
mostCurrent._btnp2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private randomN As Byte";
_randomn = (byte)0;
 //BA.debugLineNum = 33;BA.debugLine="Private turn As Byte";
_turn = (byte)0;
 //BA.debugLineNum = 35;BA.debugLine="Private btnPlay As Button";
mostCurrent._btnplay = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _p1visible(boolean _bool) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Private Sub P1Visible(bool As Boolean)";
 //BA.debugLineNum = 62;BA.debugLine="lblP1.Visible = bool";
mostCurrent._lblp1.setVisible(_bool);
 //BA.debugLineNum = 63;BA.debugLine="txtP1.Visible = bool";
mostCurrent._txtp1.setVisible(_bool);
 //BA.debugLineNum = 64;BA.debugLine="btnP1.Visible = bool";
mostCurrent._btnp1.setVisible(_bool);
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static String  _p2visible(boolean _bool) throws Exception{
 //BA.debugLineNum = 67;BA.debugLine="Private Sub P2Visible(bool As Boolean)";
 //BA.debugLineNum = 68;BA.debugLine="lblP2.Visible = bool";
mostCurrent._lblp2.setVisible(_bool);
 //BA.debugLineNum = 69;BA.debugLine="txtP2.Visible = bool";
mostCurrent._txtp2.setVisible(_bool);
 //BA.debugLineNum = 70;BA.debugLine="btnP2.Visible = bool";
mostCurrent._btnp2.setVisible(_bool);
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _turnchange() throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Private Sub turnChange()";
 //BA.debugLineNum = 108;BA.debugLine="If turn == 1 Then";
if (_turn==1) { 
 //BA.debugLineNum = 109;BA.debugLine="turn = 2";
_turn = (byte) (2);
 //BA.debugLineNum = 110;BA.debugLine="P1Visible(False)";
_p1visible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 111;BA.debugLine="P2Visible(True)";
_p2visible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="txtP2.RequestFocus";
mostCurrent._txtp2.RequestFocus();
 }else {
 //BA.debugLineNum = 114;BA.debugLine="turn = 1";
_turn = (byte) (1);
 //BA.debugLineNum = 115;BA.debugLine="P1Visible(True)";
_p1visible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="P2Visible(False)";
_p2visible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="txtP1.RequestFocus";
mostCurrent._txtp1.RequestFocus();
 };
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
}