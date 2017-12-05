package com.cmput301.t05.habilect;

/**
 * This interface is created so that upon occurence of a follow request appropriate aciton can be taken
 *
 * @author ioltuszy
 */

public interface OnFollowRequestListener {
    public void OnAccepted();

    public void OnDeclined();

    public void OnIgnored();
}
