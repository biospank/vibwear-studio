package it.vibwear.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by biospank on 22/09/15.
 */
public class AppManager {
    private static final String[] BLACK_LIST_PACKAGES = {
        "com.cleanmaster.mguard","com.cmcm.lite","com.cleanmaster.mguard_x86","com.cleanmaster.security","com.piriform.ccleaner",
        "com.cleanmaster.cleanbooster","com.davidsmart.supercleanmaster","com.cleanmaster.boost","com.itcato.phone.clean.master",
        "com.qihoo.security","com.avast.android.mobilesecurity","com.avast.android.cleaner","om.dianxinos.optimizer.duplay",
        "com.antivirus","com.ijinshan.kbatterydoctor_en","com.phone_cleaning_virus_free","com.antivirus.tablet",
        "booster.cleaner.optimizer","om.GoldenBabbler.MemoryCleanMaster","com.liquidum.thecleaner","com.meedoon.cleanphone",
        "com.meedoon.masterphoneclean","com.bensoft.cleanmaster","com.lionmobi.powerclean","clean.master.booster",
        "apps.ignisamerica.cleaner","com.meedoon.cleanmyphone","com.meedoon.phonecleaner","motiurion.apdz.itfist.supercleanmaster",
        "com.rootuninstaller.rambooster","com.CleanMaster2016.FreeCacheCleaner","com.mody.rambooster","com.cleanmaster2016.memorydubatterysaver",
        "com.systech.freecleanmaster","com.NewCleanMaster2016.ProCacheRamCleaner","com.duapps.cleaner","com.precinct.rambooster",
        "com.Fchkl.cache.cleaner","com.cleanmaster.batterysaverdoctorcheck","com.ozonikyocho.cleanmasterbooster","com.ar.apps.tools.appcleaner",
        "com.kk.cleaner","com.procapp.cleanmaster","phuc.cleanmaster.supercleanmaster","com.fangblade.cleanmasteroptimizer",
        "com.avira.optimizer","com.ramclean.powercleanram","com.mobo.clean","com.appm.memory.cleaner.and.speed.booster",
        "com.NewCleanMaster2016.TopFastRamCleaner","com.ramclean.ramcleanmaster","com.lovesclean.cleanmaster",
        "imoblife.toolbox.full","na.cleanmaster.app","at.ncn.cleanboostmaster","com.ramclean.cleanmasterboost",
        "com.gppady.cleaner","mobi.mgeek.browserfaster","com.fangblade.autocleanmaster","com.fastcleandoctor.speed.cleanmaster",
        "com.cleanmaster.battery","tools.cleaner.cleanmaster","cn.iam007.pic.clean.master","com.ccdev.cleanmemorycleanmaster",
        "om.newcleanrammaster2016.ramcleaner","com.cleeaneasy.larameaasy","com.ozonikyocho.ramcleanmaster","com.fangblade.powercleanmaster",
        "com.takeawayapps.powercleanmaster","com.gau.go.launcherex.gowidget.taskmanagerex","com.creativelabsstudio.cleanmasterforandroidboost",
        "prajwal.prakash.yescleaner_noadds","com.ozonikyocho.ramcleanmaster2016","fastclean.rambooster","com.pinnacle.cleaner",
        "com.ozonikyocho.junkcleanmaster","com.prashantdroid.cachecleaner","com.ozonikyocho.powercleanmaster","weappsoft.memorybooster.cleanmaster",
        "com.rambooster2015.memorycleanrammaster","com.neverlast.freecleanmaster","cn.com.wece.superclean","com.clean.k.aa",
        "com.meedoon.powerclean","superladyqueen.ram.clean.master","com.clean.k.lg","com.speed.speedboostercacheremover", "com.orangepoint.spacecleaner",
        "com.clean.k.huawei","com.mastercleaner.cleanmasterforandroidboost","com.clean.k.htc","battery_saver.master.clean","com.clean.master.security",
        "com.newagetools.batdoc","com.sologame.batterysaver","com.dianxinos.dxbs","com.BatteryDoctor2016.FreeBatteryDoctor","com.battery.doctor.saver",
        "com.saverbatter2016.batterysaver2016","batterydoctor.fastcharger.batterysaver","com.battery.saverpro","com.mcafee.batteryoptimizer",
        "ch.smalltech.battery.free","com.avast.android.batterysaver","com.crush.batterysaver","com.FreeBatteryDoctor.ProBattery2016",
        "com.battery.lifesave","com.dianxinos.dxbs.paid","com.macropinch.pearl","bluebrain.ovi.superchargerfree","com.lionmobi.battery",
        "com.psafe.powerpro","batterysaver.doctorpower.charger2016","com.dianxinos.optimizer.duplay","com.drweb","net.lepeng.batterydoctor",
        "com.kingbattery.batteryliferepairstar","apps.ignisamerica.batterysaver","com.qihoo.security.lite","com.vdc.batterysaver",
        "uk.org.crampton.battery","com.qihoo.security","com.geekyouup.android.widgets.battery","com.fsinib.batterymonitor",
        "com.appeteria.battery100alarm","com.batteryIndicatorFree","com.fsepi.batterysaver","com.fasteasy.battery.deepsaver",
        "com.integer.batterydoctor","com.forest.superbattery","com.avg.cleaner","com.gau.go.launcherex.gowidget.gopowermaster",
        "com.aioapp.battery","com.a0soft.gphone.aDataOnOff","com.saverbattery2015.batterysaver","com.freebatterydoctorsaver.dubatterysaver",
        "com.muddyapps.Smart.Battery.Doctor","com.zrgiu.antivirus","com.eset.ems2.gp","com.avira.android","com.wsandroid.suite",
        "com.cyou.security","com.trustport.mobilesecurity","com.lookout","com.slava.taskmanager","com.memtaskcleaner.android",
        "com.netqin.aotkiller","com.hermes.superb.booster","com.onedept.bestrambooster","com.tools.androidsystemcleaner"
    };

    private Context context;
    private String packageName;

    public static ArrayList<String> findKillerApp(Context context) {
        ArrayList<String> killerAppPackageList = new ArrayList<String>();
        List<String> blackListPackages = Arrays.asList(BLACK_LIST_PACKAGES);
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS) ;

        if(packageInfoList != null) {
            Iterator<PackageInfo> iterator = packageInfoList.iterator();

            while (iterator.hasNext()) {
                PackageInfo packageInfo = iterator.next();

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    if(blackListPackages.contains(packageInfo.packageName))
                        killerAppPackageList.add(packageInfo.packageName);
                }
            }
        }

        return killerAppPackageList;

    }

    public AppManager(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public Drawable getIconApp() {
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        return icon;

    }

    public String getAppName() {
        ApplicationInfo applicationInfo = null;

        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        if(applicationInfo != null)
            return ((String) context.getPackageManager().getApplicationLabel(applicationInfo));
        else
            return packageName;

    }

}
