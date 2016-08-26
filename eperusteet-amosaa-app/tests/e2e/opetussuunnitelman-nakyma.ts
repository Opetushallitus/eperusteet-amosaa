describe("Koulutustoimijan näkymä", () => {
    browser.get("http://amosaa:amosaa@localhost:9030/");
    browser.manage().logs().get('browser').then(browserLog => {
        console.log('browser logs: ' + require('util').inspect(browserLog));
    });

    it("Uuden opsin luominen", () => {

        expect($("button[ng-click='addOpetussuunnitelma()']").isPresent());
        const luoOps = element.all(by.buttonText("Lisää opetussuunnitelma")).get(1);
        expect(luoOps.isEnabled()).not.toBeFalsy();
        luoOps.click();

        expect($("input[ng-change=\"update(input)\"]"));
        const opsHaku = element(by.css("input[ng-change=\"update(input)\"]"));
        opsHaku.sendKeys("audio");

        const perusteetLista = element.all(by.css("div[ng-repeat=\"peruste in perusteet\"]"));
        perusteetLista.first().click();

        const opsNimi = element(by.css("input[ng-model=\"ops.nimi\"]"));
        opsNimi.sendKeys("test");

        element(by.buttonText("Tallenna")).click();

        const opsTeksti = element(by.linkText("test"));
        opsTeksti.click();

        element(by.css("a[ui-sref=\".sisalto\"]")).click();
    });

    it("Paikallisen tutkinnon osan luominen", () => {
        element(by.css("button[ng-click=\"add()\"]")).click();

        element.all(by.buttonText("Lisää")).get(1).click();

        const osaNimi = element(by.css("input[ng-model=\"obj.tekstiKappale.nimi\"]"));
        osaNimi.sendKeys("tosa");

        element(by.css("button[ng-click=\"ok(obj)\"]")).click();
    });

    it("Tutkinnon osan toteutuksen kirjoittaminen", () => {

    });

    it("Poistaminen ja palauttaminen", () => {

    });

    it("Käsitteen lisääminen", () => {

    });

    it("Dokumentin generointi", () => {

    });

    it("Versiohistoriasta palauttamien", () => {

    });
});
