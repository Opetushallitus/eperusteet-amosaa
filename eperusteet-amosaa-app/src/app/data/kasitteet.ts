namespace TermistoData {
    let _Api, _stateParams, _termisto;

    const termistoAPI = (ktId) => {
        return ktId ? _Api.all("koulutustoimijat").one(ktId).all('termisto') : null;
    };

    export const init = (Api, $stateParams) => {
        _Api = Api;
        _stateParams = $stateParams;
    };

    const getTermisto = (ktId = _stateParams.ktId) => {
        if (_termisto) {
            return _termisto;
        }
        _termisto = termistoAPI ? termistoAPI(ktId).getList(): null;
        return _termisto;
    };

    export const getAll = (ktId = _stateParams.ktId) => {
        return getTermisto(ktId);
    };

    const filterByKey = (kasitteet, key) => {
        return _.filter(kasitteet, (kasite:any) => kasite.avain === key)[0];
    };

    const getByKey = (key, termisto = _termisto) => {
        return termisto.then((kasitteet) => {
            return filterByKey(kasitteet, key);
        })
    };

    export const refresh = () => {
        _termisto = null;
        return getTermisto(_stateParams.ktId);
    };

    export const getByAvain = (avain, ktId = _stateParams.ktId) => {
        if (_termisto && getByKey(avain)) {
            return getByKey(avain);
        };
        return refresh().then((res) => getByKey(avain));
    };

    export const add = (kasite) => {
        return termistoAPI(_stateParams.ktId).post(kasite).then(() => {
            return refresh().then((res) => res);
        });
    };
}

angular.module("app")
    .run(($injector) => $injector.invoke(TermistoData.init))