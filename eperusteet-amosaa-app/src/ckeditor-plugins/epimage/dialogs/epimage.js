/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

'use strict';
/* global CKEDITOR */

CKEDITOR.dialog.add('epimageDialog', function( editor ) {
  var kaanna = editor.config.customData.kaanna;
  var controllerScope = null;
  return {
    title: kaanna('epimage-plugin-title'),
    minWidth: 400,
    minHeight: 200,
    contents: [
      {
        id: 'tab-basic',
        label: kaanna('epimage-plugin-label'),
        elements: [
          {
            type: 'html',
            id: 'epimage-html',
            validate: function() {
              return !this.getValue() ? kaanna('epimage-plugin-virhe-viite-tyhja') : true;
            },
            html:
            '<div ng-controller="EpImagePluginController" class="ckeplugin-ui-select">'+
            '  <label class="ckeditor-plugin-label">{{ \'epimage-plugin-label-epimage\' | kaanna }}</label>'+
            '  <ui-select ng-model="model.chosen" ng-if="images.length > 0">'+
            '    <ui-select-match placeholder="{{ \'epimage-plugin-select-placeholder\' | kaanna }}">'+
            '      {{ $select.selected.nimi | kaanna }}'+
            '    </ui-select-match>'+
            '    <ui-select-choices repeat="epimage in filtered track by $index"'+
            '                       refresh="filterImages($select.search)" refresh-delay="0">'+
            '      <span ng-bind-html="epimage.nimi | kaanna | highlight : $select.search"></span>'+
            '    </ui-select-choices>'+
            '  </ui-select>'+
            '  <p class="empty-epimaget" ng-if="images.length === 0" kaanna="\'ei-kuvia\'"></p>'+
            '  <div class="epimage-plugin-add">'+
            '      <div ng-if="!model.chosen">'+
            '      <label class="ckeditor-plugin-label">{{ \'epimage-plugin-lisaa-uusi\' | kaanna }}</label>'+
            '      <span ng-if="uploader.queue.length == 0"'+
            '            class="btn btn-default btn-file"'+
            '            style="position: relative;">'+
            '        <span kaanna="\'valitse\'"></span>'+
            '        <input type="file" nv-file-select nv-file-drop uploader="uploader">'+
            '      </span>'+
            '      <div ng-if="uploader.queue.length > 0">'+
            '        <button ng-click="uploader.queue[0].remove()"'+
            '                type="button"'+
            '                class="btn btn-default"'+
            '                ng-disabled="uploader.queue[0].isUploading || uploader.queue[0].isSuccess"'+
            '                kaanna="\'peruuta\'"></button>'+
            '        <button ng-click="uploader.queue[0].upload()"'+
            '                ng-disabled="uploader.queue[0].isReady || uploader.queue[0].isUploading || uploader.queue[0].isSuccess"'+
            '                type="button" class="btn btn-primary">'+
            '          <span kaanna="\'laheta\'"></span>'+
            '        </button>'+
            '        <div ng-show="uploader.isHTML5"'+
            '             ng-thumb="{ file: uploader.queue[0]._file, height: 100 }"'+
            '             class="epimage-thumb"></div>'+
            '        <p ng-show="scaleError" class="error-message">{{ scaleError | kaanna }}</p>'+
            '        </div>'+
            '      </div>'+
            '      <div ng-if="model.chosen">'+
            '        <label class="ckeditor-plugin-label">{{ \'epimage-plugin-kuvan-asetukset\' | kaanna }}</label>'+
            '        <button ng-click="model.chosen = null" class="btn btn-default"><span ng-bind="\'peruuta\' | kaanna"></span></button>'+
            '        <img ng-src="{{ model.chosen.src }}" width="{{ model.chosen.width }}" height="{{ model.chosen.height }}"'+
            '             class="epimage-thumb"></img>'+
            '        <label class="ckeditor-plugin-label">{{ \'epimage-plugin-koko\' | kaanna }}</label>'+
            '        <input ng-change="widthChange(model.chosen)"'+
            '               ng-model="model.chosen.width"'+
            '               class="form-control"'+
            '               style="max-width: 70px; float: left;">'+
            '        <span style="float: left; margin-top: 10px;">X</span>'+
            '        <input ng-change="heightChange(model.chosen)"'+
            '               ng-model="model.chosen.height"'+
            '               class="form-control"'+
            '               style="max-width: 70px; float: left;">'+
            '        <div class="clearfix"></div>'+
            '        <label class="ckeditor-plugin-label">{{ \'epimage-plugin-kuvateksti\' | kaanna }}</label>'+
            '        <input type="text"' +
            '               class="form-control"' +
            '               ng-model="model.chosen.alt">'+
            '        <p ng-show="scaleError" class="error-message">{{ scaleError | kaanna }}</p>'+
            '      </div>'+
            '    <p ng-show="message" class="success-message">{{ message | kaanna }}</p>'+
            '    <p ng-show="model.rejected.length > 0" class="error-message">'+
            '      {{ \'epimage-plugin-hylatty\' | kaanna }}'+
            '    </p>'+
            '  </div>'+
            '</div>',
            onLoad: function () {
              var self = this;
              var el = this.getElement().$;
              angular.element('body').injector().invoke(function($compile) {
                var scope = angular.element(el).scope();
                $compile(el)(scope);
                controllerScope = angular.element(el).scope();
                controllerScope.init();
                controllerScope.registerListener(function onChange(value) {
                  if (value && value.id) {
                    self.setValue(value.id);
                  }
                });
              });
            },
            onShow: function () {
              var dialog = this.getDialog();
              if (!dialog.insertMode) {
                dialog.setupContent(dialog.element);
              }
            },
            setup: function (element) {
              controllerScope.clear();
              var value = element.getAttribute('data-uid');
              controllerScope.setValue(element);
              this.setValue(value);
            },
            commit: function (element) {
              var chosen = controllerScope.getChosen();
              element.setAttribute('data-uid', this.getValue());
              element.setAttribute('src', chosen.src);
              element.setAttribute('width', chosen.width);
              element.setAttribute('height', chosen.height);
              element.setAttribute('alt', chosen.alt);
            }
          }
        ]
      }
    ],
    onShow: function () {
      var selection = editor.getSelection();
      var element = selection.getStartElement();
      if (element) {
        element = element.getAscendant('img', true);
      }
      if (!element || element.getName() !== 'img') {
        element = editor.document.createElement('img');
        element.appendText(selection.getSelectedText());
        this.insertMode = true;
      } else {
        this.insertMode = false;
      }
      this.setupContent(element);
      this.element = element;
    },
    onOk: function() {
      var el = this.element;
      this.commitContent(el);
      if (this.insertMode) {
        editor.insertElement(el);
      }
    }
  };
});
