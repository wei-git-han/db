package com.css.websocket;

public class SendPojo {

    private String userId;
    //督办
    //个人
    private boolean unit = false;
    private boolean unitIsSerf = false;
    //局内
    private boolean bureau = false;
    private boolean bureauIsSerf = false;
    //办理反馈
    private boolean feedback = false;
    private boolean feedbackIsSerf = false;

    private String waitCount;

    public void setSendPojo(String userId,int menuType,Boolean isSerf) {
        this.userId = userId;
        switch (menuType){
            case 4:
                this.unit = true;
                this.unitIsSerf = this.unitIsSerf && isSerf;
                break;
            case 5:
                this.bureau = true;
                this.bureauIsSerf = this.bureauIsSerf && isSerf;
                break;
            case 6:
                this.feedback = true;
                this.feedbackIsSerf = this.feedbackIsSerf && isSerf;
                break;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUnit() {
        return unit;
    }

    public void setUnit(boolean unit) {
        this.unit = unit;
    }

    public boolean isUnitIsSerf() {
        return unitIsSerf;
    }

    public void setUnitIsSerf(boolean unitIsSerf) {
        this.unitIsSerf = unitIsSerf;
    }

    public boolean isBureau() {
        return bureau;
    }

    public void setBureau(boolean bureau) {
        this.bureau = bureau;
    }

    public boolean isBureauIsSerf() {
        return bureauIsSerf;
    }

    public void setBureauIsSerf(boolean bureauIsSerf) {
        this.bureauIsSerf = bureauIsSerf;
    }

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    public boolean isFeedbackIsSerf() {
        return feedbackIsSerf;
    }

    public void setFeedbackIsSerf(boolean feedbackIsSerf) {
        this.feedbackIsSerf = feedbackIsSerf;
    }

    public String getWaitCount() {
        return waitCount;
    }

    public void setWaitCount(String waitCount) {
        this.waitCount = waitCount;
    }
}
