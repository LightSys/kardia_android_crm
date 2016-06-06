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

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO Sync correctly
        KardiaFetcher fetcher = new KardiaFetcher(getContext());
        String partnerId = AccountManager.get(getContext()).getUserData(account, "partnerId");
        if(partnerId != null) {
            List<Partner> collaboratees = fetcher.getCollaboratees(account);
            for (Partner collaboratee : collaboratees) {
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
                                new String[]{collaboratee.getPartnerId()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.PARTNER_NAME + " = ?",
                            new String[] {collaboratee.getPartnerName()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.PARTNER_NAME + " = ?",
                                new String[]{collaboratee.getPartnerName()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.SURNAME + " = ?",
                            new String[] {collaboratee.getSurname()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.SURNAME + " = ?",
                                new String[]{collaboratee.getSurname()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.GIVEN_NAMES + " = ?",
                            new String[] {collaboratee.getGivenNames()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.GIVEN_NAMES + " = ?",
                                new String[]{collaboratee.getGivenNames()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.PHONE + " = ?",
                            new String[] {collaboratee.getPhone()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.PHONE + " = ?",
                                new String[]{collaboratee.getPhone()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.CELL + " = ?",
                            new String[] {collaboratee.getCell()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.CELL + " = ?",
                                new String[]{collaboratee.getCell()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.EMAIL + " = ?",
                            new String[] {collaboratee.getEmail()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.EMAIL + " = ?",
                                new String[]{collaboratee.getEmail()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.ADDRESS_1 + " = ?",
                            new String[] {collaboratee.getAddress1()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.ADDRESS_1 + " = ?",
                                new String[]{collaboratee.getAddress1()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.CITY + " = ?",
                            new String[] {collaboratee.getCity()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.CITY + " = ?",
                                new String[]{collaboratee.getCity()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.STATE_PROVINCE + " = ?",
                            new String[] {collaboratee.getStateProvince()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.STATE_PROVINCE + " = ?",
                                new String[]{collaboratee.getStateProvince()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Cursor cursor = provider.query(CRMContract.CollaborateeTable.CONTENT_URI,
                            null,
                            CRMContract.CollaborateeTable.POSTAL_CODE + " = ?",
                            new String[] {collaboratee.getPostalCode()},
                            null);
                    if(cursor.moveToFirst()) {
                        provider.update(CRMContract.CollaborateeTable.CONTENT_URI,
                                values,
                                CRMContract.CollaborateeTable.POSTAL_CODE + " = ?",
                                new String[] {collaboratee.getPostalCode()});
                    } else {
                        provider.insert(CRMContract.CollaborateeTable.CONTENT_URI, values);
                    }
                    cursor.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}