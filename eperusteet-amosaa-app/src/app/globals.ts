declare var CKEDITOR: any;

type Promise<T> = angular.IPromise<T>;

interface JQueryExtensions {
    sticky(x: Object);
}

interface JQuery extends JQueryExtensions { }
