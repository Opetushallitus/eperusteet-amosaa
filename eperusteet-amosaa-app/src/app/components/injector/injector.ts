namespace InjectorService {
    let injector;

    export const init = ($injector) => injector = $injector;

    export const inject = (arr: Array<any>) => {
        let result;
        arr.push(function() {
            result = _.zipObject(arr, arguments);
        });
        injector.invoke(arr);
        return result;
    }
};

angular.module("app")
.run(($injector) => $injector.invoke(InjectorService.init));
