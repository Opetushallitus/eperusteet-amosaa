namespace TermistoData {
    let _Api, _stateParams, _termisto;

    const termistoAPI = (ktId) => {
        return _Api.all("koulutustoimijat").one(ktId).all('termisto');
    };

    export const init = (Api, $stateParams) => {
        _Api = Api;
        _stateParams = $stateParams;
    };

    const getTermisto = (ktId = _stateParams.ktId) => {
        if (_termisto) {
            return _termisto;
        }
        _termisto = termistoAPI(ktId).getList();
        return _termisto;
    };

    export const getAll = (ktId = _stateParams.ktId) => {
        return getTermisto(ktId);
    };

    const getByKey = (key, termisto = _termisto) => {
        return termisto ? _.filter(termisto, (kasite:any) => kasite.avain === key)[0] : [];
    };

    const refreshTermisto = () => {
        _termisto = termistoAPI(_stateParams.ktId);
        return _termisto.getList();
    };

    export const getByAvain = (ktId, avain) => {
        if (!_termisto) { getAll(ktId) }
        let cached = getByKey(avain, null);
        return cached ? cached : getByKey(avain, refreshTermisto());

    };

    export const set = (kasite) => {
        return termistoAPI(_stateParams.ktId).post(kasite).then(() => {
            return refreshTermisto();
        });
    };
}

angular.module("app")
    .run(($injector) => $injector.invoke(TermistoData.init))