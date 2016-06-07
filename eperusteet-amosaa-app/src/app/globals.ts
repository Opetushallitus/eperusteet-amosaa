declare var CKEDITOR: any;
declare var Chart: any;

type Promise<T> = angular.IPromise<T>;
type REl = restangular.IElement;
type RCol = restangular.ICollection;

interface JQueryExtensions {
    sticky(x: Object);
}

interface JQuery extends JQueryExtensions { }
