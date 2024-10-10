package com.adayo.systemui.functional.negative._messageAction;


import com.adayo.soavb.foundation.message.MessageAction;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.service.SoaService;

public class MessageActions {

    private static final String TAG = MessageActions.class.getSimpleName();

    private volatile static MessageActions mMessageAction;

    public static MessageActions getInstance() {
        if (mMessageAction == null) {
            synchronized (MessageActions.class) {
                if (mMessageAction == null) {
                    mMessageAction = new MessageActions();
                }
            }
        }
        return mMessageAction;
    }

    public MessageActions(){}

    private boolean isEditType = false;
    public void setEditType(boolean isEditType){
        this.isEditType = isEditType;
    }

    public boolean isEditType(){
        return isEditType;
    }

    public MessageAction<Integer> getMessageAction(SoaService soaService, String getMessage, String setMessage, String eventMessage){
        return new MessageAction<Integer>(TAG,
                new ViewBinderProviderInteger.Builder()
                        .withService(soaService)
                        .withGetMessageName(getMessage)
                        .withSetMessageName(setMessage)
                        .withEventMessageName(eventMessage)
                        .withInitialValue(-1)
                        .build());
    }
}
