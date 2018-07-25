/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
 *
 * Copyright (c) Edwin Petremann <https://github.com/Isolonice/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package common;

public interface ICooldown {
    String BIOME = "biome";
    int BIOME_DELAY = 10;

    String CONFIANCE = "confiance";
    int CONFIANCE_DELAY = 10;

    String CREATION = "creation";
    int CREATION_DELAY = 86400;

    String LISTE = "liste";
    int LISTE_DELAY = 86400;

    String MAISON = "maison";
    int MAISON_DELAY = 10;

    String METEO = "meteo";
    int METEO_DELAY = 10;

    String OFF = "off";
    int OFF_DELAY = 86400;

    String ON = "on";
    int ON_DELAY = 86400;

    String REFONTE = "refonte";
    int REFONTE_DELAY = 86400;

    String RETIRERCONFIANCE = "retirerconfiance";
    int RETIRERCONFIANCE_DELAY = 10;

    String AJOUTERCONFIANCE = "ajouterconfiance";
    int AJOUTERCONFIANCE_DELAY = 10;

    String ACCESCONFIANCE = "accesconfiance";
    int ACCESCONFIANCE_DELAY = 10;

    String TELEPORT = "teleport";
    int TELEPORT_DELAY = 86400;

    String WARP = "warp";
    int WARP_DELAY = 10;

    String TIME = "time";
    int TIME_DELAY = 10;
}
