var
    _             = require('lodash'),
    autoprefixer  = require('gulp-autoprefixer'),
    config        = require('./config.json'),
    concat        = require('gulp-concat'),
    connect       = require('gulp-connect'),
    clean         = require('gulp-clean'),
    preprocess    = require('gulp-preprocess'),
    rev           = require('gulp-rev'),
    revReplace    = require('gulp-rev-replace'),
    gulp          = require('gulp'),
    jade          = require('gulp-jade'),
    merge         = require('merge-stream'),
    proxy         = require('proxy-middleware'),
    sass          = require('gulp-sass'),
    shell         = require('gulp-shell'),
    spawn         = require('child_process').exec,
    typescript    = require('gulp-tsc'),
    url           = require('url'),
    ts_app_config = require('./tsconfig.json'),
    // tslint        = require('gulp-tslint'),
    // tslint_config = require('./tslint.json'),
    watch         = require('gulp-watch');

var tests = _.filter(ts_app_config.files, function (file) { return _.startsWith(file, 'test'); });
var sources = _.reject(ts_app_config.files, function (file) { return _.startsWith(file, 'test'); });

// TODO:
// Tests

function mkStream(fn) {
    return require('event-stream').map(fn);
}

function createProxy(from, to) {
    var pOpt = url.parse(from);
    pOpt.route = to;
    return proxy(pOpt);
}

var testReporter = function (output, file) {
    console.log("Found " + output.length + " errors in " + file.path);

    _.forEach(output, function (error) {
        console.log("    " + error.failure + " [" + error.endPosition.line + ", " + error.endPosition.character + "]");
    });
};

gulp
.task('templatepacker', function () {
    return gulp.src([
            config.app + 'misc/mixins.jade',
            config.frontend_utils + 'components/**/*.jade',
            config.app + 'views/**/*.jade',
            config.app + 'misc/guidance/**/*.jade',
            config.app + 'modals/**/*.jade',
            config.app + 'states/*.jade',
            config.app + 'states/koulutustoimija/**/*.jade',
            config.app + 'states/virhe/**/*.jade'])
        .pipe(mkStream(function (file, cb) {
            // Ruminta
            var fpath = file.path;
            if (file.path.indexOf("node_modules") !== -1) {
                fpath = fpath.slice((file.cwd + config.frontend_utils).length - 1);
            }
            else {
                fpath = fpath.slice((file.cwd + config.app).length + 1);
            }

            if (/^win/.test(process.platform)) {
                fpath = fpath.replace(/\\/g,'/');
            }

            if (fpath !== 'index.jade') {
                var prefix = 'script(type="text/ng-template" id="' + fpath + '")\n';
                file.contents = Buffer.from(prefix + '  ' + String(file.contents).replace(/\n/g, '\n  ') + '\n');
            }
            cb(null, file);
        }))
        .pipe(concat('templates.jade'))
        .pipe(gulp.dest(config.app));
})
.task('templates', ['templatepacker'], function () {
    return gulp.src([config.app + 'index.jade'])
        .pipe(jade())
        .pipe(concat('index.html'))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('ckeditor', function () {
    gulp.src(['./node_modules/ckeditor/**']).pipe(gulp.dest(config.build + '/ckeditor'));
    return gulp.src(['./src/ckeditor-plugins/**']).pipe(gulp.dest(config.build + '/ckeditor/plugins'));
})
.task('locales', function () {
    return gulp.src(['./src/localisation/**']).pipe(gulp.dest(config.build + '/localisation'));
})
.task('images', function () {
    gulp.src(['./src/app/favicon.ico']).pipe(gulp.dest(config.build));
    return gulp.src(['./src/images/**']).pipe(gulp.dest(config.build + '/images'));
})
.task('static-fonts', ['ckeditor', 'locales', 'images'], function () {
    return gulp.src([
        './node_modules/bootstrap-sass/assets/fonts/**',
        './node_modules/font-awesome/fonts/**'
    ])
    .pipe(gulp.dest(config.build + '/fonts'));
})
.task('sass', function () {
    return merge(
        gulp.src(config.styles),
            gulp.src([config.app + 'styles/styles.scss'])
                .pipe(sass().on('error', sass.logError)))
        .pipe(concat('styles.css'))
        .pipe(autoprefixer({
            browsers: ['last 1 version'],
            cascade: false
        }))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('dependencies', function () {
    return gulp.src(config.dependencies)
        .pipe(concat('amosaa-dependencies.js'))
        .pipe(gulp.dest(config.build));
})
// gulp-tsc uses file.path instead of file.contents
// .task('preprocess', function () {
//     return gulp.src(ts_app_config.files)
//         .pipe(mkStream(function (file, cb) {
//             var stateMatch = "app/states";
//             var state = file.path.indexOf(stateMatch);
//             if (state !== -1 && _.endsWith(file.path, 'state.ts')) {
//                 var stateName = file.path.slice(state + stateMatch.length);
//                 stateName = "root" + stateName.slice(0, stateName.length - "/state.ts".length).replace(/\//g, ".");
//                 file.contents = new Buffer(String(file.contents).replace(/\@StateName/g, stateName) + '\n');
//             }
//             cb(null, file);
//         }))
//         .pipe(gulp.dest('tmp/'));
// })
.task('compile', function () {
    return gulp.src(sources)
        // .pipe(tslint({ configuration: tslint_config }))
        // .pipe(tslint.report(testReporter, {
        //     emitError: false,
        //     summarizeFailureOutput: true
        // }))
        .pipe(typescript({
            target: "es3",
            noFallthroughCasesInSwitch: true,
            noImplicitReturns: true,
            lib: ["DOM", "ES2015", "ES5"]
        }))
        .on('error', function (error) { })
        .pipe(concat('amosaa-app.js'))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('connect', function () {
    return connect.server({
        root: config.build,
        port: 9030,
        middleware: function (connect, opt) {
            return [
                createProxy('https://virkailija.testivirkailija.opintopolku.fi/eperusteet-service', '/eperusteet-service'),
                createProxy('http://localhost:8090/eperusteet-amosaa-service/', '/eperusteet-amosaa-service')
            ];
        }
    });
})
.task('build', ['dependencies', 'templates', 'sass', 'compile', 'static-fonts'], function() {
    return gulp.src(config.build + 'index.html')
        .pipe(gulp.dest(config.build));
})
.task('dist:move', ['build'], function () {
    return gulp.src([config.build + '**/*'])
        .pipe(gulp.dest(config.dist));
})
.task('revision', ['dist:move'], function(){
    return gulp.src([config.dist + "/*.js"])
        .pipe(rev())
        .pipe(gulp.dest(config.dist))
        .pipe(rev.manifest())
        .pipe(gulp.dest(config.dist));
})
.task('dist', ['revision'], function(){
    var manifest = gulp.src(config.dist + "/rev-manifest.json");
    return gulp.src(config.dist + "/index.html")
        .pipe(revReplace({manifest: manifest}))
        .pipe(gulp.dest(config.dist));
})
.task('watch', ['connect', 'build'], function () {
    gulp.watch(config.app + '**/*.ts', ['compile']);
    gulp.watch(config.frontend_utils + 'components/**/*.ts', ['compile']);
    gulp.watch(config.frontend_utils + 'components/**/*.jade', ['templates']);
    gulp.watch(config.frontend_utils + 'components/**/*.scss', ['sass']);
    gulp.watch(config.app + '**/*.jade', ['templates']);
    gulp.watch(config.app + '**/*.scss', ['sass']);
    gulp.watch('./src/localisation/*.json', ['locales']);
    gulp.watch('./src/ckeditor-plugins/**/*', ['ckeditor']);
})
.task('clean', function () {
    return gulp.src([config.build, config.dist], { read: false })
        .pipe(clean({ force: true }));
})
.task('default', ['watch']);
