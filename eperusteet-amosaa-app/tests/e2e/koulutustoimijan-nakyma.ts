describe("Koulutustoimijan näkymä", () => {
    browser.get("http://amosaa:amosaa@localhost:9030/");
    browser.manage().logs().get('browser').then(browserLog => {
        console.log('browser logs: ' + require('util').inspect(browserLog));
    });

    it("Uuden yhteisen pohjan luonti", () => {

        element(by.css("h2 > span > a[uib-dropdown-toggle]")).click();

        element(by.linkText("Opetushallitus")).click();

        element.all(by.buttonText("Lisää uusi")).get(1).click();

        const pohjaNimi = element(by.css("input[ng-model=\"pohja.nimi\"]"));
        pohjaNimi.sendKeys("pohja");

        element(by.buttonText("Lisää")).click();

        element(by.css("a[ng-click=\"muutaTila()\"]")).click();
        element(by.css("div[ng-if=\"tila === 'valmis'\"]")).click();
        element(by.css("a[ng-click=\"muutaTila()\"]")).click();
        element(by.css("div[ng-if=\"tila === 'julkaistu'\"]")).click();

        element(by.css("a[ui-sref=\"root\"]")).click();

    });

    it("Uuden yhteisen osan luominen", () => {

        element.all(by.buttonText("Lisää uusi")).get(0).click();

        const pohjaNimi = element(by.css("input[ng-model=\"yhteinen.nimi\"]"));
        pohjaNimi.sendKeys("pohjan testi");

        element(by.buttonText("Lisää")).click();

        element(by.css("a[ui-sref=\"root\"]")).click();

    });

    it("Jaetun tutkinnon luominen", () => {

        element.all(by.buttonText("Lisää jaettu osa")).get(1).click();

        const opsHaku = element(by.css("input[ng-model=\"ops.nimi\"]"));
        opsHaku.sendKeys("jaettu");

        element(by.buttonText("Tallenna")).click();

        element(by.css("a[ui-sref=\"root\"]")).click();

    });
});
