namespace KommentitService {
  let _$rootScope, _$q, _$log, _$timeout;

  export const init = ($rootScope, $q, $log, $timeout) => {
    _$rootScope = $rootScope;
    _$timeout = $timeout;
    _$log = $log;
    _$q = $q;
  };

  let _activeCallbacks: any;
  const defaultCallbacks = () => ({
    start:  (val) => _$q((resolve, reject) => resolve(val)),
    save:   (val) => _$q((resolve, reject) => resolve(val)),
    cancel: (res) => _$q((resolve, reject) => resolve(res)),
    after:  _.noop,
    done: _.noop
  });

  const stop = () => _$q((resolve) => {
    _$rootScope.$$ekEditing = false;
  });

  const handleError = (reject) => ((err) => {
    NotifikaatioService.varoitus(err.status + "! Voi rähmä :(");
    console.error(err);
    return reject(err);
  });

  export const lisaaKommentti = (kommentti, parent) => {
    console.log("add comment", kommentti, parent);
  };

  const start = (callbacks, isGlobal: boolean) => _$q((resolve, reject) => {
    callbacks = _.merge(defaultCallbacks(), callbacks);
    if (_$rootScope.$$ekEditing) {
      return reject();
    }
    else {
      return callbacks.start()
          .then(res => {
            _$rootScope.$$ekEditing = true;
            _activeCallbacks = callbacks;
            _$rootScope.$broadcast("editointikontrollit:disable");
            if (isGlobal) {
              _$rootScope.$broadcast("editointikontrollit:start");
            }
            return resolve(res);
          })
          .catch(handleError(reject));
    }
  });

  export const save = (kommentti?) => _$q((resolve, reject) => _activeCallbacks
      .save(kommentti)
      .then((res) => {
        _$rootScope.$broadcast("editointikontrollit:saving");
        _$rootScope.$$ekEditing = false;
        _$rootScope.$broadcast("editointikontrollit:enable");
        _$rootScope.$broadcast("editointikontrollit:cancel");
        _activeCallbacks.after(res);
        _activeCallbacks.done();
      })
      .catch(handleError(reject)));

  export const cancel = () => _$q((resolve, reject) => {
    _$rootScope.$broadcast("editointikontrollit:canceling");
    return _activeCallbacks.cancel()
        .then(() => {
          _$rootScope.$$ekEditing = false;
          _$rootScope.$broadcast("editointikontrollit:enable");
          _$rootScope.$broadcast("editointikontrollit:cancel");
          resolve();
        })
        .catch(reject);
  });

  export const createRestangular = (
      scope,
      field: string,
      resolvedObj: restangular.IElement,
      callbacks: IEditointikontrollitCallbacks = {}) => {
    scope[field] = resolvedObj.clone();
    return EditointikontrollitService.create(_.merge({
      start: () => _$q((resolve, reject) => scope[field].get()
          .then(res => {
            _.merge(resolvedObj, res);
            scope[field] = resolvedObj.clone();
            resolve();
          })
          .catch(reject)),
      save: (kommentti) => _$q((resolve, reject) => {
        _$rootScope.$broadcast("notifyCKEditor");
        scope[field].kommentti = kommentti;
        return scope[field].put()
            .then((res) => {
              NotifikaatioService.onnistui("tallennus-onnistui");
              return resolve(res);
            })
            .catch(reject);
      }),
      cancel: (res) => _$q((resolve, reject) => {
        scope[field] = resolvedObj.clone();
        resolve();
      }),
      after: (res) => _.merge(resolvedObj, res),
    }, callbacks));
  };

  export const isEnabled = () => !!_activeCallbacks;
  export const isEditing = () => _$rootScope.$$ekEditing;
  export const create = (callbacks = {}) => _.partial(start, callbacks, true);
  export const createLocal = (callbacks = {}) => _.partial(start, callbacks, false);
}

module Kommentit {

  export const directive = ($parse, $timeout) => {

    return {
      restrict: 'E',
      templateUrl: 'components/kommentit/kommentit.jade',
      scope: {
      },
      controller: function($scope) {
        $scope.editoi = false;
        $scope.editoitava = '';
        $scope.sisalto = false;

        $scope.nayta = false;
        $scope.editointi = false;
        $scope.onLataaja = false;
        $scope.urlit = {};

        //$scope.nimikirjaimet = kayttajaToiminnot.nimikirjaimet;
        //
        //$scope.$kommenttiMaxLength = {
        //  maara: YleinenData.kommenttiMaxLength
        //};
        $scope.kommentit = Fake.Kommentit();

        function lataaKommentit(url) {
          var lataaja = $scope.urlit[url];
          if (lataaja) {
            lataaja(function(kommentit) {
              $scope.sisalto = kommentit;
              $scope.nayta = true;
            });
          }
        }

        $scope.$on('$stateChangeStart', function() {
          $scope.nayta = false;
          $scope.onLataaja = false;
        });

        function lataajaCb(url, lataaja) {
          if (!$scope.urlit[url]) {
            $scope.onLataaja = true;
            $scope.urlit[url] = lataaja;
          }
        }

        //var stored = Kommentit.stored();
        //if (!_.isEmpty(stored)) {
        //  lataajaCb(stored.url, stored.lataaja);
        //}

        $scope.$on('update:kommentit', function(event, url, lataaja) {
          lataajaCb(url, lataaja);
        });

        //$scope.naytaKommentit = function() { lataaKommentit($location.url()); };
        //$scope.muokkaaKommenttia = function(kommentti, uusikommentti, cb) {
        //  Kommentit.muokkaaKommenttia(kommentti, uusikommentti, cb);
        //};
        //$scope.poistaKommentti = function(kommentti) {
        //  Varmistusdialogi.dialogi({
        //    otsikko: 'vahvista-poisto',
        //    teksti: 'poistetaanko-kommentti',
        //    primaryBtn: 'poista',
        //    successCb: function () {
        //      Kommentit.poistaKommentti(kommentti);
        //    }
        //  })();
        //};

        $scope.lisaaKommentti = function(parent, kommentti, cb) {
          console.log("lisää kommentti", kommentti, parent);
          $scope.editointi = true;
          KommentitService.lisaaKommentti(parent, kommentti);
          //Kommentit.lisaaKommentti(parent, kommentti, function() {
          //  $scope.sisalto.$yhteensa += 1;
            (cb || angular.noop)();
          //});
        };

        $scope.$on('enableEditing', function() {
          $scope.editointi = true;
        });
        $scope.$on('disableEditing', function() {
          $scope.editointi = false;
        });
        $timeout(function () {
          //$scope.naytaKommentit();
        });
      }
    };

  }
}

angular.module("app")
    .run(($injector) => $injector.invoke(KommentitService.init))
    .directive("kommentit", Kommentit.directive);
