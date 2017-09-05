namespace TermistoData {
    let i, _termisto;

    const termistoAPI = ktId => {
        return i.Api
            .all("koulutustoimijat")
            .one(ktId)
            .all("termisto");
    };

    export const init = $injector => {
        i = inject($injector, ["Api", "$stateParams", "$q"]);
    };

    const getTermisto = (ktId = i.$stateParams.ktId) => {
        var deferred = i.$q.defer();
        if (_termisto) {
            deferred.resolve(_termisto);
        } else {
            _termisto = termistoAPI(ktId).getList();
            deferred.resolve(_termisto);
        }
        return deferred.promise;
    };

    export const getAll = (ktId = i.$stateParams.ktId) => {
        return getTermisto(ktId);
    };

    const filterByKey = (kasitteet, key) => {
        return _.filter(kasitteet, (kasite: any) => kasite.avain === key)[0];
    };

    const getByKey = (key, termisto = _termisto) => {
        return termisto.then(kasitteet => {
            return filterByKey(kasitteet, key);
        });
    };

    export const refresh = () => {
        _termisto = null;
        return getTermisto(i.$stateParams.ktId);
    };

    export const getByAvain = (avain, ktId = i.$stateParams.ktId) => {
        if (_termisto && getByKey(avain)) {
            return getByKey(avain);
        }
        return refresh().then(res => getByKey(avain));
    };

    export const add = kasite => {
        return termistoAPI(i.$stateParams.ktId)
            .post(kasite)
            .then(() => {
                return refresh().then(res => res);
            });
    };
}

angular.module("app").run($injector => $injector.invoke(TermistoData.init));
