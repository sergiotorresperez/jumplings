package com.garrapeta.jumplings.game.weapon;

import com.garrapeta.jumplings.game.JumplingsGameWorld;

public class WeaponFactory {

    public static Weapon getWeapon(JumplingsGameWorld jgWorld, short weaponId) {
        switch (weaponId) {
        case FingerprintWeapon.WEAPON_CODE_FINGERPRINT:
            return new FingerprintWeapon(jgWorld);
        case SwordWeapon.WEAPON_CODE_SWORD:
            return new SwordWeapon(jgWorld, jgWorld);
        }
        throw new IllegalStateException("Not such weapon: " + weaponId);
    }

}
