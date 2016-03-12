package org.lightsys.crmapp.data;

/**
 * Created by nathan on 3/10/16.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("sync", "yes");
        KardiaFetcher fetcher = new KardiaFetcher(getContext());
        Log.d("serverSync", AccountManager.get(getContext()).getUserData(account, "server"));
        List<Staff> staff = fetcher.getStaff(account);
        for(Staff staffMember : staff) {
            ContentValues values = new ContentValues();
            Log.d("kardiaLogin", staffMember.getKardiaLogin());
            values.put(CRMContract.StaffTable.PARTNER_ID, staffMember.getPartnerId());
            values.put(CRMContract.StaffTable.KARDIA_LOGIN, staffMember.getKardiaLogin());
            try {
                provider.insert(CRMContract.StaffTable.CONTENT_URI, values);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        String partnerId = AccountManager.get(getContext()).getUserData(account, "partnerId");
        if(partnerId != null) {
            List<Partner> collaboratees = fetcher.getCollaboratees(account);
            for (Partner collaboratee : collaboratees) {
                Log.d("collobarateeSync", collaboratee.getPartnerName());
                fetcher.getCollaborateeInfo(account, collaboratee);
                ContentValues values = new ContentValues();
                values.put(CRMContract.CollaborateeTable.COLLABORATER_ID, partnerId);
                values.put(CRMContract.CollaborateeTable.PARTNER_ID, collaboratee.getPartnerId());
                values.put(CRMContract.CollaborateeTable.PARTNER_NAME, collaboratee.getPartnerName());
                values.put(CRMContract.CollaborateeTable.SURNAME, collaboratee.getSurname());
                values.put(CRMContract.CollaborateeTable.GIVEN_NAMES, collaboratee.getGivenNames());
                values.put(CRMContract.CollaborateeTable.PHONE, collaboratee.getPhone());
                values.put(CRMContract.CollaborateeTable.CELL, collaboratee.getCell());
                values.put(CRMContract.CollaborateeTable.EMAIL, collaboratee.getEmail());
                values.put(CRMContract.CollaborateeTable.ADDRESS_1, collaboratee.getAddress1());
                values.put(CRMContract.CollaborateeTable.CITY, collaboratee.getCity());
                values.put(CRMContract.CollaborateeTable.STATE_PROVINCE, collaboratee.getStateProvince());
                values.put(CRMContract.CollaborateeTable.POSTAL_CODE, collaboratee.getPostalCode());
                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.PARTNER_ID + " = ?",
                            new String[] {collaboratee.getPartnerId()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.PARTNER_ID + " = ?",
                                new String[] {collaboratee.getPartnerId()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}