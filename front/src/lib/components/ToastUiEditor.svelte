<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
	import { filterObjectKeys, getUrlParams, stripIndent } from '$lib/utils';

	// 추가적인 CSS 임포트
	import '@toast-ui/editor/dist/toastui-editor.css';
	import 'prismjs/themes/prism.css';
	import '@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css';
	import '@toast-ui/chart/dist/toastui-chart.css';
	import 'tui-color-picker/dist/tui-color-picker.css';
	import '@toast-ui/editor-plugin-color-syntax/dist/toastui-editor-plugin-color-syntax.css';
	import '@toast-ui/editor-plugin-table-merged-cell/dist/toastui-editor-plugin-table-merged-cell.css';

	const {
		body,
		viewer = false,
		height = '300px',
		saveBody
	} = $props<{
		body: string;
		viewer?: boolean;
		height?: string;
		saveBody?: Function;
	}>();

	let editorDivWrap: HTMLDivElement | undefined = $state();
	let editorDiv: HTMLDivElement | undefined = $state();
	let editor: any;

	let isFullScreen = false;

	function toggleFullScreen() {
		if (!editorDivWrap || !editorDiv) return;

		if (viewer && window.document.body.classList.contains('fullscreen-editor')) return;
		if (!viewer && window.document.body.classList.contains('fullscreen-viewer')) return;

		isFullScreen = !isFullScreen;
		window.document.body.classList.toggle('fullscreen-mode');

		if (viewer) window.document.body.classList.toggle('fullscreen-viewer');
		else window.document.body.classList.toggle('fullscreen-editor');

		window.document.body.classList.toggle('overflow-hidden');

		editorDivWrap.classList.toggle('fixed');
		editorDivWrap.classList.toggle('top-0');
		editorDivWrap.classList.toggle('left-0');
		editorDivWrap.classList.toggle('w-full');
		editorDivWrap.classList.toggle('!h-[100dvh]');
		if (!viewer) editorDiv.classList.toggle('!h-[100%]');
		editorDivWrap.classList.toggle('overflow-y-scroll');
		editorDivWrap.classList.toggle('bg-white');
		editorDivWrap.classList.toggle('z-10');

		if (viewer) editorDivWrap.classList.toggle('p-2');
	}

	function switchTab() {
		editorDiv!
			.querySelectorAll('.toastui-editor-tabs > .tab-item:not(.active)')
			.forEach((element: Element): void => {
				// 각 요소에 대해 클릭 이벤트를 발생시킴
				(element as HTMLElement).click();
			});
	}

	rq.effect(async () => {
		const [
			{ default: Editor },
			{ default: codeSyntaxHighlight },
			{ default: chart },
			{ default: colorSyntax },
			{ default: tableMergedCell },
			{ default: uml },
			{}
		] = await Promise.all([
			// @ts-ignore
			import('@toast-ui/editor'),
			import(
				// @ts-ignore
				'@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all'
			),
			import('@toast-ui/editor-plugin-chart'),
			import('@toast-ui/editor-plugin-color-syntax'),
			import('@toast-ui/editor-plugin-table-merged-cell'),
			import('@toast-ui/editor-plugin-uml'),
			// @ts-ignore
			import('@toast-ui/editor/dist/i18n/ko-kr')
		]);

		const chartOptions = {
			minWidth: 100,
			maxWidth: 600,
			minHeight: 100,
			maxHeight: 300
		};

		const umlOptions = {
			rendererURL: 'http://www.plantuml.com/plantuml/svg/'
		};

		function configPlugin() {
			const toHTMLRenderers = {
				config(node: any) {
					return [
						{ type: 'openTag', tagName: 'div', outerNewLine: true },
						{ type: 'html', content: '' },
						{ type: 'closeTag', tagName: 'div', outerNewLine: true }
					];
				}
			};

			return { toHTMLRenderers };
		}

		function hidePlugin() {
			const toHTMLRenderers = {
				hide(node: any) {
					return [
						{ type: 'openTag', tagName: 'div', outerNewLine: true },
						{ type: 'html', content: '' },
						{ type: 'closeTag', tagName: 'div', outerNewLine: true }
					];
				}
			};

			return { toHTMLRenderers };
		}

		function youtubePlugin() {
			const toHTMLRenderers = {
				youtube(node: any) {
					const html = renderYoutube(node.literal);

					return [
						{ type: 'openTag', tagName: 'div', outerNewLine: true },
						{ type: 'html', content: html },
						{ type: 'closeTag', tagName: 'div', outerNewLine: true }
					];
				}
			};

			function renderYoutube(url: string) {
				url = url.replace('https://www.youtube.com/watch?v=', '');
				url = url.replace('http://www.youtube.com/watch?v=', '');
				url = url.replace('www.youtube.com/watch?v=', '');
				url = url.replace('youtube.com/watch?v=', '');
				url = url.replace('https://youtu.be/', '');
				url = url.replace('http://youtu.be/', '');
				url = url.replace('youtu.be/', '');

				let urlParams = getUrlParams(url);

				let width = '100%';
				let height = '100%';

				let maxWidth = '500';

				if (urlParams['max-width']) {
					maxWidth = urlParams['max-width'];
				}

				let ratio = 'aspect-[16/9]';

				let marginLeft = 'auto';

				if (urlParams['margin-left']) {
					marginLeft = urlParams['margin-left'];
				}

				let marginRight = 'auto';

				if (urlParams['margin-right']) {
					marginRight = urlParams['margin-right'];
				}

				let youtubeId = url;

				if (youtubeId.indexOf('?') !== -1) {
					let pos = url.indexOf('?');
					youtubeId = youtubeId.substring(0, pos);
				}

				return (
					'<div style="max-width:' +
					maxWidth +
					'px; margin-left:' +
					marginLeft +
					'; margin-right:' +
					marginRight +
					';" class="' +
					ratio +
					' relative"><iframe class="absolute top-0 left-0 w-full" width="' +
					width +
					'" height="' +
					height +
					'" src="https://www.youtube.com/embed/' +
					youtubeId +
					'" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe></div>'
				);
			}

			return { toHTMLRenderers };
		}

		function codepenPlugin() {
			const toHTMLRenderers = {
				codepen(node: any) {
					const html = renderCodepen(node.literal);

					return [
						{ type: 'openTag', tagName: 'div', outerNewLine: true },
						{ type: 'html', content: html },
						{ type: 'closeTag', tagName: 'div', outerNewLine: true }
					];
				}
			};

			function renderCodepen(url: string) {
				const urlParams = getUrlParams(url);

				let height = '400';

				if (urlParams.height) {
					height = urlParams.height;
				}

				let width = '100%';

				if (urlParams.width) {
					width = urlParams.width;
				}

				if (!width.includes('px') && !width.includes('%')) {
					width += 'px';
				}

				let iframeUri = url;

				if (iframeUri.indexOf('#') !== -1) {
					let pos = iframeUri.indexOf('#');
					iframeUri = iframeUri.substring(0, pos);
				}

				return (
					'<iframe height="' +
					height +
					'" style="width: ' +
					width +
					';" title="" src="' +
					iframeUri +
					'" allowtransparency="true" allowfullscreen="true"></iframe>'
				);
			}

			return { toHTMLRenderers };
		}

		const editorConfig = {
			plugins: [
				codeSyntaxHighlight,
				[chart, chartOptions],
				colorSyntax,
				tableMergedCell,
				[uml, umlOptions],
				configPlugin,
				hidePlugin,
				youtubePlugin,
				codepenPlugin
			],
			customHTMLRenderer: {
				heading(node: any, { entering, getChildrenText }: any) {
					return {
						type: entering ? 'openTag' : 'closeTag',
						tagName: `h${node.level}`,
						attributes: {
							id: getChildrenText(node).trim()
						}
					};
				},
				htmlBlock: {
					iframe(node: any) {
						const newAttrs = filterObjectKeys(node.attrs, [
							'src',
							'width',
							'height',
							'allow',
							'allowfullscreen',
							'frameborder',
							'scrolling'
						]);

						return [
							{
								type: 'openTag',
								tagName: 'iframe',
								outerNewLine: true,
								attributes: newAttrs
							},
							{ type: 'html', content: node.childrenHTML },
							{ type: 'closeTag', tagName: 'iframe', outerNewLine: false }
						];
					}
				}
			},
			initialValue: body
		};

		editor = viewer
			? Editor.factory({
					el: editorDiv,
					viewer,
					...editorConfig
				})
			: new Editor({
					el: editorDiv,
					height,
					initialEditType: 'markdown',
					previewStyle: 'tab',
					useCommandShortcut: false,
					events: {
						keydown: function (mode: any, event: any) {
							if (event.isComposing == false && event.isTrusted) {
								if (event.ctrlKey && event.shiftKey && (event.key == 'z' || event.key == 'Z')) {
									// 윈도우 : Ctrl + Shift + z 를 누르면 redo
									editor.exec('redo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (
									event.metaKey &&
									event.shiftKey &&
									(event.key == 'z' || event.key == 'Z')
								) {
									// MAC : Cmd + Shift + z 를 누르면 redo
									editor.exec('redo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.ctrlKey && event.key == 'y') {
									// 윈도우 : Ctrl + y 를 누르면 redo
									editor.exec('redo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.metaKey && event.key == 'y') {
									// MAC : Cmd + y 를 누르면 redo
									editor.exec('redo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.ctrlKey && event.key == 'z') {
									// 윈도우 : Ctrl + z 를 누르면 undo
									editor.exec('undo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.metaKey && event.key == 'z') {
									// MAC : Cmd + z 를 누르면 undo
									editor.exec('undo');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.ctrlKey && event.key == 'q') {
									// 윈도우 : Ctrl + q 를 누르면 toggleFullScreen
									editor.exec('toggleFullScreen');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.metaKey && event.key == 'q') {
									// MAC : Cmd + q 를 누르면 toggleFullScreen
									editor.exec('toggleFullScreen');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.ctrlKey && event.key == 's') {
									// 윈도우 : Ctrl + s 를 누르면 saveBody
									editor.exec('saveBody');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.metaKey && event.key == 's') {
									// MAC : Cmd + s 를 누르면 saveBody
									editor.exec('saveBody');

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.ctrlKey && event.key == 'd') {
									// 윈도우 : Ctrl + d 를 누르면

									event.stopPropagation();
									event.preventDefault();
									return;
								} else if (event.metaKey && event.key == 'd') {
									// MAC : Cmd + d 를 누르면

									event.stopPropagation();
									event.preventDefault();
									return;
								}
							}
						}
					},
					language: 'ko-KR',
					placeholder: stripIndent(`내용을 입력해주세요.`).trim(),
					...editorConfig
				});

		saveBody &&
			editor.addCommand &&
			editor.addCommand('markdown', 'saveBody', () => {
				saveBody();

				return true;
			});

		editor.addCommand &&
			editor.addCommand('markdown', 'toggleFullScreen', () => {
				toggleFullScreen();
				return true;
			});

		editor.addCommand &&
			editor.addCommand('markdown', 'openImageUploader', () => {
				window.open('https://postimages.org');
				return true;
			});

		editor.insertToolbarItem &&
			editor.insertToolbarItem(
				{ groupIndex: 0, itemIndex: 0 },
				{
					name: 'toggleFullScreen',
					tooltip: '풀스크린(Ctrl + q, Cmd + q)',
					className: '!text-[20px]',
					text: 'F',
					command: 'toggleFullScreen'
				}
			);

		saveBody &&
			editor.insertToolbarItem &&
			editor.insertToolbarItem(
				{ groupIndex: 0, itemIndex: 0 },
				{
					name: 'saveBody',
					tooltip: '저장(Ctrl + s, Cmd + s)',
					className: '!text-[20px]',
					text: 'S',
					command: 'saveBody'
				}
			);

		editor.removeToolbarItem && editor.removeToolbarItem('image');
		editor.insertToolbarItem &&
			editor.insertToolbarItem(
				{ groupIndex: 3, itemIndex: 1 },
				{
					name: 'img',
					tooltip:
						'이미지 업로드 후 생성된 URL 을 `![](Img Direct link)` 형식으로 에디터에 입력하시면 됩니다.',
					className: 'toastui-editor-toolbar-icons',
					style: { backgroundPosition: '-309px 3px' },
					command: 'openImageUploader'
				}
			);
	});

	rq.effect(() => {
		return () => {
			if (isFullScreen) toggleFullScreen();

			editor.destroy();
		};
	});

	export { editor, switchTab, toggleFullScreen, viewer, isFullScreen };
</script>

<div bind:this={editorDivWrap}>
	<slot name="beforeContent" />
	<div bind:this={editorDiv}></div>
	<slot name="afterContent" />
</div>
