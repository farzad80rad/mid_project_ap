
package GUI;

/**
 * this enum is for storing the info of settings. just choose a thing and call its info.
 */
public enum SettingInfo {
    theme(2),weight(1600),height(1000),frameX(100),frameY(50),toggleSideBar(true),fullScreen(false),flowReDirect(true),terminateOnExit(true);
    Object info;
    SettingInfo(Object info)
    {
        this.info = info;
    }

    /**
     * get info of this enum obj.
     * @return info to be set. for (theme , weight and .. is an int , and for toggleSIdeBar , fullScreen and ... is a boolean.
     */
    public Object getInfo() {
        return info;
    }

    /**
     * set info for this enum obj.
     * @param info info to be set. for (theme , weight and .. is an int , and for toggleSIdeBar , fullScreen and ... is a boolean.
     */
    public void setInfo(Object info) {
        this.info = info;
    }
}
