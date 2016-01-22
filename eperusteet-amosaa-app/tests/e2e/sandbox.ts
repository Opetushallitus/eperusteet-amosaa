describe('Sandbox components should work', () => {
    browser.get('http://localhost:9030/#/fi/sandbox');

    it('should translate objects', () => {
        expect($('div[kaanna=obj]').getText()).toEqual('Hei');
    });

    it('Editointikontrollit pitäisi toimia globaalilla valitsimella', () => {
        const muokkaa = element.all(by.buttonText('Muokkaa')).get(1);
        expect(muokkaa).not.toBeFalsy();
    });
});
