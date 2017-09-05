declare var CKEDITOR: any;
declare var Chart: any;

type REl = restangular.IElement;
type RCol = restangular.ICollection;

interface JQueryExtensions {
    sticky(x: Object);
}

interface JQuery extends JQueryExtensions {}
