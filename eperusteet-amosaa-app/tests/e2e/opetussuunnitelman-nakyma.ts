describe("Koulutustoimijan näkymä", () => {

    it("Uuden opsin luominen", () => {
        browser.get("http://amosaa:amosaa@localhost:9030/");

        expect($("button[ng-click='addOpetussuunnitelma()']").isPresent());
        const luoOps = element.all(by.buttonText("Opetussuunnitelma")).get(1);
        expect(luoOps.isEnabled()).not.toBeFalsy();
        luoOps.click();

        expect($("input[ng-change=\"update(input)\"]"));
        const opsHaku = element(by.css("input[ng-change=\"update(input)\"]"));
        opsHaku.sendKeys("audio");

        const perusteetLista = element.all(by.css("div[ng-repeat=\"peruste in perusteet\"]"));
        perusteetLista.first().click();

        const opsNimi = element(by.css("input[ng-model=\"ops.nimi\"]"));
        opsNimi.sendKeys("test");

        element(by.css("button[ng-click=\"ok(ops)\"]")).click();

        const opsTeksti = element(by.linkText("test"));
        opsTeksti.click();

        element(by.css("a[ui-sref=\".sisalto\"]")).click();

    });

    it("Paikallisen tutkinnon osan luominen", () => {
        element(by.css("button[ng-click=\"add()\"]")).click();
        element.all(by.buttonText("Lisää")).get(1).click();
        element(by.css("input[ng-model=\"obj.tekstiKappale.nimi\"]")).sendKeys("tosa");
        element(by.css("button[ng-click=\"ok(obj)\"]")).click();
    });

    it("Tutkinnon osan toteutuksen kirjoittaminen", () => {
        element(by.css("button[ng-click=\"edit()\"]")).click();
        element(by.css("div[ng-model=\"osa.tekstiKappale.teksti\"]")).sendKeys("tosan sisältö");
        element(by.css("input[ng-model=\"kommentti\"]")).sendKeys("lisätty sisältö tosaan");
        element(by.css("button[ng-click=\"save()\"]")).click();
    });

    it("Poistaminen ja palauttaminen", () => {
        element(by.css("button[ng-click=\"edit()\"]")).click();
        element(by.css("button[ng-click=\"remove()\"]")).click();
        element(by.css("button[ng-click=\"ok()\"]")).click();
        element(by.css("button[ng-click=\"palauta(poistettu)\"]")).click();
    });

    it("Käsitteen lisääminen ja poistaminen", () => {
        element(by.css("a[ui-sref=\".kasitteet\"]")).click();
        element(by.css("button[ng-click=\"createKasite()\"]")).click();
        element(by.css("input[ng-model=\"newKasite.termi\"]")).sendKeys("käsite 1");
        element(by.css("div[ng-model=\"newKasite.selitys\"]")).sendKeys("Käsitteen 1 selitys");
        element(by.css("button[ng-click=\"postKasite(newKasite)\"]")).click();
        element(by.css("button[ng-click=\"remove(kasite)\"]")).click();
        element(by.css("button[ng-click=\"ok()\"]")).click();
    });

    it("Dokumentin generointi", () => {
        element(by.css("a[ui-sref=\".dokumentti\"]")).click();
        element(by.css("button[ng-click=\"generoi()\"]")).click();
        expect(element(by.linkText("Lataa PDF")).getTagName()).toBe("a");
    });

    it("Versiohistoriasta palauttamien", () => {
        element(by.css("a[ui-sref=\".sisalto\"]")).click();
        element(by.css("button[ng-click=\"add()\"]")).click();

        element.all(by.buttonText("Lisää")).get(0).click();
        element(by.css("input[ng-model=\"obj.tekstiKappale.nimi\"]")).sendKeys("tekstikappale");
        element(by.css("button[ng-click=\"ok(obj)\"]")).click();
        element(by.css("button[ng-click=\"edit()\"]")).click();

        const tkTeksti = element(by.css("div[ng-model=\"osa.tekstiKappale.teksti\"]"));

        tkTeksti.clear();
        tkTeksti.sendKeys("tekstikappaleen vanha sisältö");

        element(by.css("button[ng-click=\"save()\"]")).click();
        element(by.css("button[ng-click=\"edit()\"]")).click();

        tkTeksti.clear();
        tkTeksti.sendKeys("tekstikappaleen uusi sisältö");

        element(by.css("button[ng-click=\"save()\"]")).click();
        element(by.css("a[ng-click=\"listRevisions()\"]")).click();
        element(by.linkText("2")).click();
        element(by.css("button[ng-click=\"restoreRevision()\"]")).click();

    });
});
